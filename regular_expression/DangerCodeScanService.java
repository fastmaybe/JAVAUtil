package com.secmask.web.risk_scan.service.riskscan;

import com.mysql.jdbc.exceptions.jdbc4.CommunicationsException;
import com.secmask.dao.service.DangercodeScanResultDao;
import com.secmask.dao.service.RiskScanDetailDao;
import com.secmask.dao.service.RiskScanLogDao;
import com.secmask.dao.service.RiskSubitemDao;
import com.secmask.pojo.DTO.ErrorEnum;
import com.secmask.pojo.PO.DangercodeScanResult;
import com.secmask.pojo.PO.RiskScanDetail;
import com.secmask.pojo.PO.RiskScanLog;
import com.secmask.pojo.PO.RiskSubitem;
import com.secmask.pojo.consts.DangerCodeType;
import com.secmask.pojo.consts.RiskLevel;
import com.secmask.pojo.consts.RiskScanStatus;
import com.secmask.web.common.util.AsynchronousRegex;
import com.secmask.web.common.util.StrIfEmpty;
import com.secmask.web.common.util.dbconnection.DataBaseTool;
import com.secmask.web.common.util.dbconnection.DbConnection;
import com.secmask.web.risk_scan.service.RiskScanAPI;
import com.secmask.web.risk_scan.util.RiskException;
import org.apache.coyote.http2.ConnectionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.sql.SQLException;
import java.sql.SQLRecoverableException;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service("dangerCodeScanService")
public class DangerCodeScanService implements RiskScanAPI {

    private static final Logger logger = LoggerFactory.getLogger(DangerCodeScanService.class);

    @Resource
    private RiskSubitemDao riskSubitemDao;

    @Resource
    private RiskScanDetailDao riskScanDetailDao;

    @Resource
    private DbConnection dbConnection;
    @Resource
    private RiskScanLogDao riskScanLogDao;
    @Resource
    private DangercodeScanResultDao dangercodeScanResultDao;

    private static Pattern riskHigh = Pattern.compile("^.+?(?:\\s+?|[']\\s*?)(DROP|drop|TRANCATE|trancate|ALTER|alter)\\s+?(table |TABLE ).+?$");
    private static Pattern middle = Pattern.compile("^.+?(?:\\s+?|[']\\s*?)(INSERT|insert)\\s+?(INTO |into ).+?$|^.+?(?:\\s+?|[']\\s*?)(delete|DELETE)\\s+?(from |FROM ).+?$|^.+?(?:\\s+?|[']\\s*?)(update|UPDATE)( ).+?$");

    /**
     * 危险程序扫描
     *
     * @param riskScanLog
     * @param dbId
     * @param riskItemId
     * @param itemId      风险子项id
     */
    @Override
    public void riskScan(RiskScanLog riskScanLog, Short dbId, int riskItemId, int itemId) throws RiskException{
        logger.info("****************开始扫描-危险程序****************");
        String err = "";
        RiskException riskException = new RiskException(ErrorEnum.SERVICE_RUN_ERROR);
        //2 获取需要扫描的风险子项
        List<RiskSubitem> riskSubItems = riskSubitemDao.queryByRiskItemId(itemId);
        RiskScanDetail riskScanDetail = new RiskScanDetail();
        //3 循环扫描子项
        try {
            // 3,1 获取连接扫描
            DataBaseTool dataBaseTool = dbConnection.createDataBaseTool(dbId);
            for (RiskSubitem riskItem : riskSubItems) {
                //3.2 记录子项扫描详情表
                riskScanDetail = setRiskScanDetail(riskScanLog.getId(), riskItem.getId());
                riskScanDetailDao.insert(riskScanDetail);
                // 3.3 获取扫描
                List<Map<String, String>> result = dataBaseTool.queryDangerCode("", DangerCodeType.valueOf(riskItem.getName()).getValue());
                //判断result返回结果是否为空  是的话直接return
                if (null==result){
                    return;
                }
                // 3.4 判断风险和等级  存入扫描结果
                judgeRisk(result, riskScanDetail.getId(), DangerCodeType.valueOf(riskItem.getName()).getValue());
                // 3.5 更新扫描详情
                riskScanDetail.setStatus(RiskScanStatus.COMPLETE.getValue());
                riskScanDetailDao.update(riskScanDetail);
                // 更新扫描记录
                riskScanLog.setSubitemScannednum(riskScanLog.getSubitemScannednum() + 1);
                riskScanLogDao.update(riskScanLog);
            }
            logger.info("****************危险程序-扫描完成****************");
        } catch (OutOfMemoryError e) {
            err = ErrorEnum.MEMORY_OUT.getMsg();
            riskException = new RiskException(ErrorEnum.MEMORY_OUT);
        } catch (CommunicationsException e) {
            err = ErrorEnum.DATABASE_LINK_FIELD.getMsg();
            riskException = new RiskException(ErrorEnum.DATABASE_LINK_FIELD);
        } catch (ConnectionException | SQLRecoverableException e){
            err = ErrorEnum.DATABASE_TIME_OUT.getMsg();
            riskException = new RiskException(ErrorEnum.DATABASE_TIME_OUT);
        } catch (SQLException e){
            err = ErrorEnum.DATABASE_SEARCH_ERROR.getMsg();
            riskException = new RiskException(ErrorEnum.DATABASE_SEARCH_ERROR);
        } catch (Exception e) {
            err = ErrorEnum.SERVICE_RUN_ERROR.getMsg();
            riskException = new RiskException(ErrorEnum.SERVICE_RUN_ERROR);
        }
        if(!"".equals(err)) {
            riskScanDetail.setErrorMsg(err);
            riskScanDetail.setStatus(RiskScanStatus.ERROR.getValue());
            riskScanDetailDao.update(riskScanDetail);
            throw riskException;
        }
    }

    /**
     * 记录扫描详情表
     *
     * @return
     */
    private RiskScanDetail setRiskScanDetail(int scanLogId, int riskSubItemId) {
        RiskScanDetail riskScanDetail = new RiskScanDetail();
        riskScanDetail.setRiskScanLog(scanLogId);
        riskScanDetail.setRiskSubitem(riskSubItemId);
        riskScanDetail.setStatus(RiskScanStatus.RUNNING.getValue());
        return riskScanDetail;
    }

    /**
     * 评判风险等级 添加结果
     *
     * @param result
     */
    private void judgeRisk(List<Map<String, String>> result, int scanDetailId, String type) {
        for (Map<String, String> e : result) {
            //首先将 e.get("code")换掉换行符换成空格，去掉两边空格  再进行匹配
            String trim = e.get("code").replaceAll("\\n|\\r", " ").trim();
            if (AsynchronousRegex.createMatcherWithTimeout(trim, riskHigh, 100).matches()) {
                Matcher ma = riskHigh.matcher(trim);
                boolean matches = ma.matches();
                String groupOne = ma.group(1);
                String groupTwo = ma.group(2);
                if (StrIfEmpty.isEmpty(groupOne) && StrIfEmpty.isEmpty(groupTwo)) {
                    //非空
                    String riskCode = groupOne + " " + groupTwo;
                    DangercodeScanResult dangerResult = setDangerCodeScanResult(e, scanDetailId, "高", type, "包含了DDL语句", riskCode);
                    dangercodeScanResultDao.insert(dangerResult);
                }
                //高风险 记录扫描结果
            } else if (AsynchronousRegex.createMatcherWithTimeout(trim, middle, 100).matches()) {
                //中风险
                Matcher matcher = middle.matcher(trim);
                matcher.matches();
                String groupOne = matcher.group(1);
                String groupTwo = matcher.group(2);
                if (StrIfEmpty.isEmpty(groupOne) && StrIfEmpty.isEmpty(groupTwo)) {
                    //非空
                    String riskCode = groupOne + " " + groupTwo;
                    DangercodeScanResult dangerResult = setDangerCodeScanResult(e, scanDetailId, "中", type, "包含了DML语句", riskCode);
                    dangercodeScanResultDao.insert(dangerResult);
                }
            }
        }
    }


    private DangercodeScanResult setDangerCodeScanResult(Map<String, String> map, int scanDetailId, String riskLevel, String type, String description, String riskCode) {
        DangercodeScanResult scanResult = new DangercodeScanResult();
        scanResult.setRiskScanDetail(scanDetailId);
        scanResult.setRiskLevel(RiskLevel.valueOf(riskLevel).getValue());
        scanResult.setName(map.get("name"));
        scanResult.setType(type);
        scanResult.setCode(map.get("code"));
        scanResult.setDescription(description);
        scanResult.setRiskCode(riskCode);
        return scanResult;
    }

}
