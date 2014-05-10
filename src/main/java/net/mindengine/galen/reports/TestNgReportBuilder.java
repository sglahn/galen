package net.mindengine.galen.reports;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

public class TestNgReportBuilder {
    
    private Configuration freemarkerConfiguration = new Configuration();

    private TestIdGenerator testIdGenerator = new TestIdGenerator();

    public void build(List<GalenTestInfo> tests, String reportPath) throws IOException, TemplateException {
        List<GalenTestAggregatedInfo> aggregatedTests = new LinkedList<GalenTestAggregatedInfo>();
        
        for (GalenTestInfo test : tests) {
            GalenTestAggregatedInfo aggregatedInfo = new GalenTestAggregatedInfo(testIdGenerator.generateTestId(test.getName()), test);
            aggregatedTests.add(aggregatedInfo);
        }
        
        exportTesngReport(aggregatedTests, reportPath);
    }

    private void exportTesngReport(List<GalenTestAggregatedInfo> tests, String reportPath) throws IOException, TemplateException {
        File file = new File(reportPath);
        makeSurePathExists(file);
        file.createNewFile();
        
        FileWriter fileWriter = new FileWriter(file);
        Map<String, Object> model = new HashMap<String, Object>();
        model.put("tests", tests);
        
        Template template = new Template("report-main", new InputStreamReader(getClass().getResourceAsStream("/testng-report/testng-report.ftl.xml")), freemarkerConfiguration);
        template.process(model, fileWriter);
        fileWriter.flush();
        fileWriter.close();
    }
    
    private void makeSurePathExists(File file) throws IOException {
        File parentDir = file.getParentFile();
        if (!parentDir.exists()) {
            if (!parentDir.mkdirs()) {
                throw new IOException("Could not create path: " + parentDir.getAbsolutePath());
            }
        }
    }

}
