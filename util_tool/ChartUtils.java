package com.secmask.util.tool;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.StandardChartTheme;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.DateTickUnit;
import org.jfree.chart.axis.DateTickUnitType;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.block.BlockBorder;
import org.jfree.chart.labels.*;
import org.jfree.chart.plot.*;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.category.LineAndShapeRenderer;
import org.jfree.chart.renderer.category.StackedBarRenderer;
import org.jfree.chart.renderer.category.StandardBarPainter;
import org.jfree.chart.renderer.xy.StandardXYBarPainter;
import org.jfree.chart.renderer.xy.XYBarRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.chart.ui.RectangleInsets;
import org.jfree.chart.ui.TextAnchor;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.time.Day;
import org.jfree.data.time.TimeSeries;

import java.awt.*;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.Vector;


public class ChartUtils {
    private static String NO_DATA_MSG = "数据加载失败";
    private static Font FONT = new Font("宋体", 0, 12);
    public static Color[] CHART_COLORS = new Color[]{new Color(31, 129, 188), new Color(92, 92, 97), new Color(144, 237, 125), new Color(255, 188, 117), new Color(153, 158, 255), new Color(255, 117, 153), new Color(253, 236, 109), new Color(128, 133, 232), new Color(158, 90, 102), new Color(255, 204, 102)};

    static {
        setChartTheme();
    }

    public ChartUtils() {
    }


    public static void setChartTheme() {
        StandardChartTheme chartTheme = new StandardChartTheme("CN");
        chartTheme.setExtraLargeFont(FONT);
        chartTheme.setRegularFont(FONT);
        chartTheme.setLargeFont(FONT);
        chartTheme.setSmallFont(FONT);
        chartTheme.setTitlePaint(new Color(51, 51, 51));
        chartTheme.setSubtitlePaint(new Color(85, 85, 85));
        chartTheme.setLegendBackgroundPaint(Color.WHITE);
        chartTheme.setLegendItemPaint(Color.BLACK);
        chartTheme.setChartBackgroundPaint(Color.WHITE);
        Paint[] OUTLINE_PAINT_SEQUENCE = new Paint[]{Color.WHITE};
        DefaultDrawingSupplier drawingSupplier = new DefaultDrawingSupplier(CHART_COLORS, CHART_COLORS, OUTLINE_PAINT_SEQUENCE, DefaultDrawingSupplier.DEFAULT_STROKE_SEQUENCE, DefaultDrawingSupplier.DEFAULT_OUTLINE_STROKE_SEQUENCE, DefaultDrawingSupplier.DEFAULT_SHAPE_SEQUENCE);
        chartTheme.setDrawingSupplier(drawingSupplier);
        chartTheme.setPlotBackgroundPaint(Color.WHITE);
        chartTheme.setPlotOutlinePaint(Color.WHITE);
        chartTheme.setLabelLinkPaint(new Color(8, 55, 114));
        chartTheme.setLabelLinkStyle(PieLabelLinkStyle.CUBIC_CURVE);
        chartTheme.setAxisOffset(new RectangleInsets(5.0D, 12.0D, 5.0D, 12.0D));
        chartTheme.setDomainGridlinePaint(new Color(192, 208, 224));
        chartTheme.setRangeGridlinePaint(new Color(192, 192, 192));
        chartTheme.setBaselinePaint(Color.WHITE);
        chartTheme.setCrosshairPaint(Color.BLUE);
        chartTheme.setAxisLabelPaint(new Color(51, 51, 51));
        chartTheme.setTickLabelPaint(new Color(67, 67, 72));
        chartTheme.setBarPainter(new StandardBarPainter());
        chartTheme.setXYBarPainter(new StandardXYBarPainter());
        chartTheme.setItemLabelPaint(Color.black);
        chartTheme.setThermometerPaint(Color.white);
        ChartFactory.setChartTheme(chartTheme);
    }

    public static void setAntiAlias(JFreeChart chart) {
        chart.setTextAntiAlias(false);
    }

    public static void setLegendEmptyBorder(JFreeChart chart) {
        chart.getLegend().setFrame(new BlockBorder(Color.WHITE));
    }

    public static DefaultCategoryDataset createDefaultCategoryDataset(Vector<Serie> series, String[] categories) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        Iterator var4 = series.iterator();

        while(true) {
            String name;
            Vector data;
            do {
                do {
                    do {
                        if (!var4.hasNext()) {
                            return dataset;
                        }

                        Serie serie = (Serie)var4.next();
                        name = serie.getName();
                        data = serie.getData();
                    } while(data == null);
                } while(categories == null);
            } while(data.size() != categories.length);

            for(int index = 0; index < data.size(); ++index) {
                String value = data.get(index) == null ? "" : data.get(index).toString();
                if (isPercent(value)) {
                    value = value.substring(0, value.length() - 1);
                }

                if (isNumber(value)) {
                    dataset.setValue(Double.parseDouble(value), name, categories[index]);
                }
            }
        }
    }

    public static DefaultPieDataset createDefaultPieDataset(String[] categories, Object[] datas) {
        DefaultPieDataset dataset = new DefaultPieDataset();

        for(int i = 0; i < categories.length && categories != null; ++i) {
            String value = datas[i].toString();
            if (isPercent(value)) {
                value = value.substring(0, value.length() - 1);
            }

            if (isNumber(value)) {
                dataset.setValue(categories[i], Double.valueOf(value));
            }
        }

        return dataset;
    }

    public static TimeSeries createTimeseries(String category, Vector<Object[]> dateValues) {
        TimeSeries timeseries = new TimeSeries(category);
        if (dateValues != null) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            Iterator var5 = dateValues.iterator();

            while(var5.hasNext()) {
                Object[] objects = (Object[])var5.next();
                Date date = null;

                try {
                    date = dateFormat.parse(objects[0].toString());
                } catch (ParseException var10) {
                }

                String sValue = objects[1].toString();
                double dValue = 0.0D;
                if (date != null && isNumber(sValue)) {
                    dValue = Double.parseDouble(sValue);
                    timeseries.add(new Day(date), dValue);
                }
            }
        }

        return timeseries;
    }

    public static void setLineRender(CategoryPlot plot, boolean isShowDataLabels) {
        setLineRender(plot, isShowDataLabels, false);
    }

    public static void setLineRender(CategoryPlot plot, boolean isShowDataLabels, boolean isShapesVisible) {
        plot.setNoDataMessage(NO_DATA_MSG);
        plot.setInsets(new RectangleInsets(10.0D, 10.0D, 0.0D, 10.0D), false);
        LineAndShapeRenderer renderer = (LineAndShapeRenderer)plot.getRenderer();
        renderer.setDefaultStroke(new BasicStroke(1.5F));
        if (isShowDataLabels) {
            renderer.setDefaultItemLabelsVisible(true);
            renderer.setDefaultItemLabelGenerator(new StandardCategoryItemLabelGenerator("{2}", NumberFormat.getInstance()));
            renderer.setDefaultPositiveItemLabelPosition(new ItemLabelPosition(ItemLabelAnchor.OUTSIDE1, TextAnchor.BOTTOM_CENTER));
        }

        renderer.setDefaultShapesVisible(isShapesVisible);
        setXAixs(plot);
        setYAixs(plot);
    }

    public static void setTimeSeriesRender(Plot plot, boolean isShowData, boolean isShapesVisible) {
        XYPlot xyplot = (XYPlot)plot;
        xyplot.setNoDataMessage(NO_DATA_MSG);
        xyplot.setInsets(new RectangleInsets(10.0D, 10.0D, 5.0D, 10.0D));
        XYLineAndShapeRenderer xyRenderer = (XYLineAndShapeRenderer)xyplot.getRenderer();
        xyRenderer.setDefaultItemLabelGenerator(new StandardXYItemLabelGenerator());
        xyRenderer.setDefaultShapesVisible(false);
        if (isShowData) {
            xyRenderer.setDefaultItemLabelsVisible(true);
            xyRenderer.setDefaultItemLabelGenerator(new StandardXYItemLabelGenerator());
            xyRenderer.setDefaultPositiveItemLabelPosition(new ItemLabelPosition(ItemLabelAnchor.OUTSIDE1, TextAnchor.BOTTOM_CENTER));
        }

        xyRenderer.setDefaultShapesVisible(isShapesVisible);
        DateAxis domainAxis = (DateAxis)xyplot.getDomainAxis();
        domainAxis.setAutoTickUnitSelection(false);
        DateTickUnit dateTickUnit = new DateTickUnit(DateTickUnitType.YEAR, 1, new SimpleDateFormat("yyyy-MM"));
        domainAxis.setTickUnit(dateTickUnit);
        StandardXYToolTipGenerator xyTooltipGenerator = new StandardXYToolTipGenerator("{1}:{2}", new SimpleDateFormat("yyyy-MM-dd"), new DecimalFormat("0"));
        xyRenderer.setDefaultToolTipGenerator(xyTooltipGenerator);
        setXY_XAixs(xyplot);
        setXY_YAixs(xyplot);
    }

    public static void setTimeSeriesRender(Plot plot, boolean isShowData) {
        setTimeSeriesRender(plot, isShowData, false);
    }

    public static void setTimeSeriesBarRender(Plot plot, boolean isShowDataLabels) {
        XYPlot xyplot = (XYPlot)plot;
        xyplot.setNoDataMessage(NO_DATA_MSG);
        XYBarRenderer xyRenderer = new XYBarRenderer(0.1D);
        xyRenderer.setDefaultItemLabelGenerator(new StandardXYItemLabelGenerator());
        if (isShowDataLabels) {
            xyRenderer.setDefaultItemLabelsVisible(true);
            xyRenderer.setDefaultItemLabelGenerator(new StandardXYItemLabelGenerator());
        }

        StandardXYToolTipGenerator xyTooltipGenerator = new StandardXYToolTipGenerator("{1}:{2}", new SimpleDateFormat("yyyy-MM-dd"), new DecimalFormat("0"));
        xyRenderer.setDefaultToolTipGenerator(xyTooltipGenerator);
        setXY_XAixs(xyplot);
        setXY_YAixs(xyplot);
    }

    public static void setBarRenderer(CategoryPlot plot, boolean isShowDataLabels) {
        plot.setNoDataMessage(NO_DATA_MSG);
        plot.setInsets(new RectangleInsets(10.0D, 10.0D, 5.0D, 10.0D));
        BarRenderer renderer = (BarRenderer)plot.getRenderer();
        renderer.setDefaultItemLabelGenerator(new StandardCategoryItemLabelGenerator());
        renderer.setMaximumBarWidth(0.075D);
        if (isShowDataLabels) {
            renderer.setDefaultItemLabelsVisible(true);
        }

        setXAixs(plot);
        setYAixs(plot);
    }

    public static void setStackBarRender(CategoryPlot plot) {
        plot.setNoDataMessage(NO_DATA_MSG);
        plot.setInsets(new RectangleInsets(10.0D, 10.0D, 5.0D, 10.0D));
        StackedBarRenderer renderer = (StackedBarRenderer)plot.getRenderer();
        renderer.setDefaultItemLabelGenerator(new StandardCategoryItemLabelGenerator());
        plot.setRenderer(renderer);
        setXAixs(plot);
        setYAixs(plot);
    }

    public static void setXAixs(CategoryPlot plot) {
        Color lineColor = new Color(31, 121, 170);
        plot.getDomainAxis().setAxisLinePaint(lineColor);
        plot.getDomainAxis().setTickMarkPaint(lineColor);
    }

    public static void setYAixs(CategoryPlot plot) {
        Color lineColor = new Color(192, 208, 224);
        ValueAxis axis = plot.getRangeAxis();
        axis.setAxisLinePaint(lineColor);
        axis.setTickMarkPaint(lineColor);
        axis.setAxisLineVisible(false);
        axis.setTickMarksVisible(false);
        plot.setRangeGridlinePaint(new Color(192, 192, 192));
        plot.setRangeGridlineStroke(new BasicStroke(1.0F));
        plot.getRangeAxis().setUpperMargin(0.1D);
        plot.getRangeAxis().setLowerMargin(0.1D);
    }

    public static void setXY_XAixs(XYPlot plot) {
        Color lineColor = new Color(31, 121, 170);
        plot.getDomainAxis().setAxisLinePaint(lineColor);
        plot.getDomainAxis().setTickMarkPaint(lineColor);
    }

    public static void setXY_YAixs(XYPlot plot) {
        Color lineColor = new Color(192, 208, 224);
        ValueAxis axis = plot.getRangeAxis();
        axis.setAxisLinePaint(lineColor);
        axis.setTickMarkPaint(lineColor);
        axis.setAxisLineVisible(false);
        axis.setTickMarksVisible(false);
        plot.setRangeGridlinePaint(new Color(192, 192, 192));
        plot.setRangeGridlineStroke(new BasicStroke(1.0F));
        plot.setDomainGridlinesVisible(false);
        plot.getRangeAxis().setUpperMargin(0.12D);
        plot.getRangeAxis().setLowerMargin(0.12D);
    }

    public static void setPieRender(Plot plot) {
        plot.setNoDataMessage(NO_DATA_MSG);
        plot.setInsets(new RectangleInsets(10.0D, 10.0D, 5.0D, 10.0D));
        PiePlot piePlot = (PiePlot)plot;
        piePlot.setInsets(new RectangleInsets(0.0D, 0.0D, 0.0D, 0.0D));
        piePlot.setCircular(true);
        piePlot.setLabelGap(0.01D);
        piePlot.setInteriorGap(0.05D);
        piePlot.setLegendItemShape(new Rectangle(10, 10));
        piePlot.setIgnoreNullValues(true);
        piePlot.setLabelBackgroundPaint((Paint)null);
        piePlot.setLabelShadowPaint((Paint)null);
        piePlot.setLabelOutlinePaint((Paint)null);
        piePlot.setShadowPaint(new Color(31, 129, 188));
        piePlot.setLabelGenerator(new StandardPieSectionLabelGenerator("{0}:{2}"));
    }

    public static boolean isPercent(String str) {
        return str != null ? str.endsWith("%") && isNumber(str.substring(0, str.length() - 1)) : false;
    }

    public static boolean isNumber(String str) {
        return str != null ? str.matches("^[-+]?(([0-9]+)((([.]{0})([0-9]*))|(([.]{1})([0-9]+))))$") : false;
    }
}