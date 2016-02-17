package com.filth.util;

import java.io.IOException;
import java.io.StringWriter;

import javax.annotation.Resource;

import org.springframework.stereotype.Component;
import org.springframework.ui.ModelMap;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;
import org.springframework.web.servlet.view.json.JsonView;

import freemarker.template.Template;
import freemarker.template.TemplateException;

/**
 * Utility class for generic creation and manipulation of ModelAndViews
 * (including ModelAndViews for JSON when returning ajax calls, for example).
 */
@Component
public final class ModelAndViewUtil {
    
    private static class ModelKey {
        public static final String SUCCESS = "success";
        public static final String ERROR_MESSAGE = "errorMessage";
        public static final String HTML_CONTENT = "html";
    }

    @Resource
    private FreeMarkerConfigurer _freeMarkerConfigurer;
    @Resource
    private JsonView _jsonView;
    
    public ModelAndView createErrorJsonModelAndView(String message) {
        return createErrorJsonModelAndView(message, new ModelMap());
    }

    public ModelAndView createErrorJsonModelAndView(String message, ModelMap mm) {
        mm.addAttribute(ModelKey.SUCCESS, false);
        mm.addAttribute(ModelKey.ERROR_MESSAGE, message);
        return new ModelAndView(_jsonView, mm);
    }

    public ModelAndView createSuccessJsonModelAndView() {
        return createSuccessJsonModelAndView(new ModelMap());
    }

    public ModelAndView createSuccessJsonModelAndView(ModelMap mm) {
        mm.addAttribute(ModelKey.SUCCESS, true);
        return new ModelAndView(_jsonView, mm);
    }

    public ModelAndView createSuccessJsonModelAndViewWithHtml(String view)
            throws TemplateException, IOException {
        return createSuccessJsonModelAndViewWithHtml(view, new ModelMap());
    }

    public ModelAndView createSuccessJsonModelAndViewWithHtml(String view, ModelMap mm)
            throws TemplateException, IOException {
        ModelMap jsonMm = new ModelMap();
        return createSuccessJsonModelAndViewWithHtml(view, mm, jsonMm);
    }

    public ModelAndView createSuccessJsonModelAndViewWithHtml(String view,
            ModelMap htmlModel, ModelMap jsonModel) throws TemplateException, IOException {
        String htmlString = renderViewAsString(htmlModel, view);
        jsonModel.addAttribute(ModelKey.SUCCESS, true);
        jsonModel.addAttribute(ModelKey.HTML_CONTENT, htmlString);
        
        return new ModelAndView(_jsonView, jsonModel);
    }

    public String renderViewAsString(ModelMap mm, String view) throws TemplateException, IOException {
        StringWriter sw = new StringWriter();
        
        Template template = _freeMarkerConfigurer.getConfiguration().getTemplate(view + ".ftl");
        template.process(mm, sw);
        
        return sw.toString();
    }
}
