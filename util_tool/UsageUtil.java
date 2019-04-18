package com.secmask.util.tool;

import com.sun.management.OperatingSystemMXBean;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.lang.management.ManagementFactory;
import java.math.BigDecimal;


/**
 * windows、Linux系统CPU、内存使用率获取
 *
 * @Author: yhy
 * @Date: 2019/3/14
 */
public class UsageUtil {

    private static final String WINDOWS_OS_NAME = "windows";
    private static final String LINUX_OS_NAME = "linux";
    private static final int CPU_TIME = 1;
    private static final int PERCENT = 100;
    private static final int FAULT_LENGTH = 10;

    /**
     * 获取CPU使用率
     *
     * @return 20.00
     */
    public static double getCpuUsage() {
        double cpuUsage = 0;

        String os = getOSName();
        if (os.startsWith(WINDOWS_OS_NAME)) {
            cpuUsage = getWindowsCpuUsage();
        } else if (os.startsWith(LINUX_OS_NAME)) {
            cpuUsage = getLinuxCpuUsage();
        }

        return cpuUsage;
    }

    /**
     * 获取内存总量/空闲
     *
     * @return 10.12
     */
    public static double getMemoryUsage() {
        double memoryUsage = 0.0;

        String os = getOSName();
        if (os.startsWith(WINDOWS_OS_NAME)) {
            memoryUsage = getWindowsTotalAndFreeMemory();
        } else if (os.startsWith(LINUX_OS_NAME)) {
            memoryUsage = getLinuxTotalAndFreeMemory();
        }

        return memoryUsage;
    }

    /**
     * 获取Linux系统CPU使用率
     *
     * @return 11.11
     */
    private static double getLinuxCpuUsage() {
        double cpuUsage = 0;

        Runtime rt = Runtime.getRuntime();
        BufferedReader br = null;

        try {
            // 调用系统的“top"命令
            Process p = rt.exec("top -b -n 1");
            br = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String str;
            String[] strArray;

            while ((str = br.readLine()) != null) {
                int m = 0;
                // 只分析正在运行的进程，top进程本身除外 &&
                if (str.contains(" R ")) {

                    strArray = str.split(" ");
                    for (String tmp : strArray) {
                        if (tmp.trim().length() == 0) {
                            continue;
                        }
                        // 第9列为cpu的使用百分比
                        if (++m == 9) {
                            cpuUsage += Double.parseDouble(tmp);
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (null != br) {
                    br.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return cpuUsage;
    }

    /**
     * 获取windows系统CPU使用率
     *
     * @return 11.11
     */
    private static double getWindowsCpuUsage() {
        try {
            String procCmd = System.getenv("windir")
                    + "\\system32\\wbem\\WMIC.exe process get Caption,CommandLine,"
                    + "KernelModeTime,ReadOperationCount,ThreadCount,UserModeTime,WriteOperationCount";
            // 取进程信息
            long[] c0 = readCpu(Runtime.getRuntime().exec(procCmd));
//            Thread.sleep(CPU_TIME);
            long[] c1 = readCpu(Runtime.getRuntime().exec(procCmd));
            if (c0 != null && c1 != null) {
                long idletime = c1[0] - c0[0];
                long busytime = c1[1] - c0[1];
                return (double) (PERCENT * (busytime) / (busytime + idletime));
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return 0;
    }

    /**
     * 读取CPU信息
     *
     * @param proc {}
     * @return long[]
     */
    private static long[] readCpu(final Process proc) {
        long[] retn = new long[2];
        try {
            InputStreamReader ir = new InputStreamReader(proc.getInputStream());
            LineNumberReader input = new LineNumberReader(ir);
            String line = input.readLine();
            if (line == null || line.length() < FAULT_LENGTH) {
                return null;
            }
            int capidx = line.indexOf("Caption");
            int cmdidx = line.indexOf("CommandLine");
            int rocidx = line.indexOf("ReadOperationCount");
            int umtidx = line.indexOf("UserModeTime");
            int kmtidx = line.indexOf("KernelModeTime");
            int wocidx = line.indexOf("WriteOperationCount");
            long idletime = 0;
            long kneltime = 0;
            long usertime = 0;
            while ((line = input.readLine()) != null) {
                if (line.length() < wocidx) {
                    continue;
                }
                // 字段出现顺序：Caption,CommandLine,KernelModeTime,ReadOperationCount,ThreadCount,UserModeTime,WriteOperation
                String caption = substring(line, capidx, cmdidx - 1);
                String cmd = substring(line, cmdidx, kmtidx - 1);
                if (cmd.contains("WMIC.exe")) {
                    continue;
                }

                if ("System Idle Process".equals(caption) || "System".equals(caption)) {
                    idletime += Long.valueOf(substring(line, kmtidx, rocidx - 1));
                    idletime += Long.valueOf(substring(line, umtidx, wocidx - 1));
                    continue;
                }

                kneltime += Long.valueOf(substring(line, kmtidx, rocidx - 1));
                usertime += Long.valueOf(substring(line, umtidx, wocidx - 1));
            }
            retn[0] = idletime;
            retn[1] = kneltime + usertime;
            return retn;
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            try {
                proc.destroy();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    /**
     * 处理字符串
     */
    public static String substring(String src, int startIdx, int endIdx) {
        byte[] b = src.getBytes();

        StringBuilder sb = new StringBuilder();
        for (int i = startIdx; i <= endIdx; i++) {
            sb.append((char) b[i]);
        }

        String trim = sb.toString().trim();
        if ("".equals(trim)) {
            return "0";
        }
        return trim;
    }

    /**
     * 获取Linux系统内存使用率
     *
     * @return 11.11
     */
    private static double getLinuxMemoryUsage() {
        double memoryUsage = 0;

        Runtime rt = Runtime.getRuntime();
        BufferedReader br = null;

        try {
            // 调用系统的“top"命令
            Process p = rt.exec("top -b -n 1");
            br = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String str;
            String[] strArray;

            while ((str = br.readLine()) != null) {
                int m = 0;
                // 只分析正在运行的进程，top进程本身除外 &&
                if (str.contains(" R ")) {
                    strArray = str.split(" ");
                    for (String tmp : strArray) {
                        if (tmp.trim().length() == 0) {
                            continue;
                        }
                        if (++m == 10) {
                            // 第10列为mem的使用百分比(RedHat 9)
                            memoryUsage += Double.parseDouble(tmp);
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (null != br) {
                    br.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return memoryUsage;
    }

    private static double getLinuxTotalAndFreeMemory() {
        Long memTotal = 0L;
        Long memFree = 0L;

        Runtime rt = Runtime.getRuntime();
        BufferedReader br = null;

        try {
            // 调用系统的“top"命令
            Process p = rt.exec("cat /proc/meminfo");
            br = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String str;
            String[] strArray;

            while ((str = br.readLine()) != null) {

                if (str.contains("MemTotal")) {
                    // 内存总量
                    strArray = str.split(":");
                    for (String tmp : strArray) {
                        if (tmp.trim().length() == 0) {
                            continue;
                        }

                        if (!tmp.trim().contains("MemTotal")) {
                            memTotal = Long.valueOf(tmp);
                        }

                    }
                }

                if (str.contains("MemFree")) {
                    // 剩余内存
                    strArray = str.split(":");
                    for (String tmp : strArray) {
                        if (tmp.trim().length() == 0) {
                            continue;
                        }

                        if (!tmp.trim().contains("MemFree")) {
                            memFree = Long.valueOf(tmp);
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (null != br) {
                    br.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        BigDecimal result = new BigDecimal((memTotal - memFree) * 100).divide(new BigDecimal(memTotal), 2, BigDecimal.ROUND_HALF_UP);

        return result.doubleValue();
    }

    /**
     * 获取windows系统内存
     *
     * @return 11.11
     */
    private static double getWindowsTotalAndFreeMemory() {
        int kb = 1024;
        OperatingSystemMXBean osmxb = (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();

        // 总的物理内存
        long totalMemorySize = osmxb.getTotalPhysicalMemorySize() / kb;
        // 剩余的物理内存
        long freePhysicalMemorySize = osmxb.getFreePhysicalMemorySize() / kb;
        // 已使用的物理内存
        long usedMemory = (totalMemorySize - freePhysicalMemorySize);

        BigDecimal result = new BigDecimal(usedMemory * 100).divide(new BigDecimal(totalMemorySize), 2, BigDecimal.ROUND_HALF_UP);

        return result.doubleValue();
    }

    /**
     * 获取当前操作系统名称. return 操作系统名称 例如:windows xp,linux 等.
     */
    private static String getOSName() {
        return System.getProperty("os.name").toLowerCase();
    }

    public static void main(String[] args) {
        double cpuUsage = getCpuUsage();
        System.out.println("CPU使用率============>" + cpuUsage);

        double memoryUsage = getMemoryUsage();
        System.out.println("内存总量/空闲=========>" + memoryUsage);
    }
}
