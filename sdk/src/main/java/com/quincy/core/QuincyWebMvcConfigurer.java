package com.quincy.core;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.quincy.core.web.GeneralInterceptor;
import com.quincy.core.web.QuincyAuthInterceptor;
import com.quincy.core.web.SignatureInterceptor;
import com.quincy.core.web.StaticInterceptor;
import com.quincy.core.web.VCodeController;
import com.quincy.core.web.freemarker.AttributeTemplateDirectiveModelBean;
import com.quincy.core.web.freemarker.I18NTemplateDirectiveModelBean;
import com.quincy.core.web.freemarker.LocaleTemplateDirectiveModelBean;
import com.quincy.core.web.freemarker.PropertiesTemplateDirectiveModelBean;
import com.quincy.sdk.Constants;
import com.quincy.sdk.annotation.CustomizedBeforeAuthInterceptor;
import com.quincy.sdk.annotation.CustomizedInterceptor;

@Configuration
public class QuincyWebMvcConfigurer implements WebMvcConfigurer {
	@Autowired
	private ApplicationContext applicationContext;
	@Value("${env}")
	private String env;
	@Value("${access-control-allow-origin:#{null}}")
	private String accessControlAllowOrigin;
	@Autowired(required = false)
	private QuincyAuthInterceptor quincyAuthInterceptor;
	@Autowired(required = false)
	private PublicKeyGetter publicKeyGetter;
	@Autowired
	private VCodeController vCodeInterceptor;
	@Value("#{'${uris.interceptor-exclusion:#{null}}'.split(',')}")
	private String[] flexibleExclusionUris;
	private final static String[] FIXED_EXCLUSION_PATH_PATTERN_S = new String[] {"/static/**", "/vcode/**", "/auth/**", "/failure", "/success"};
	private static String[] EXCLUSION_PATH_PATTERN_S = null;

	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		if(Constants.ENV_DEV.equals(env))
			registry.addInterceptor(new StaticInterceptor()).addPathPatterns("/static/**");
		registry.addInterceptor(new GeneralInterceptor(accessControlAllowOrigin)).addPathPatterns("/**");
		Map<String, Object> map = applicationContext.getBeansWithAnnotation(CustomizedBeforeAuthInterceptor.class);
		for(Object interceptor:map.values()) {
			CustomizedBeforeAuthInterceptor annotation = interceptor.getClass().getDeclaredAnnotation(CustomizedBeforeAuthInterceptor.class);
			InterceptorRegistration registration = registry.addInterceptor((HandlerInterceptor)interceptor)
					.addPathPatterns(annotation.pathPatterns())
					.excludePathPatterns(getExclusionPathPattens())
					.order(annotation.order());
			String[] excludePathPatterns = annotation.excludePathPatterns();
			if(excludePathPatterns!=null&&excludePathPatterns.length>0)
				registration.excludePathPatterns(excludePathPatterns);
		}
		if(publicKeyGetter!=null)
			registry.addInterceptor(new SignatureInterceptor(publicKeyGetter)).addPathPatterns("/**").excludePathPatterns(getExclusionPathPattens());
		registry.addInterceptor(vCodeInterceptor).addPathPatterns("/**").excludePathPatterns(getExclusionPathPattens());
		if(quincyAuthInterceptor!=null) {
			HandlerInterceptorAdapter handlerInterceptorAdapter = (HandlerInterceptorAdapter)quincyAuthInterceptor;
			registry.addInterceptor(handlerInterceptorAdapter).addPathPatterns("/**").excludePathPatterns(getExclusionPathPattens());
		}
		map = applicationContext.getBeansWithAnnotation(CustomizedInterceptor.class);
		for(Object interceptor:map.values()) {
			CustomizedInterceptor annotation = interceptor.getClass().getDeclaredAnnotation(CustomizedInterceptor.class);
			InterceptorRegistration registration = registry.addInterceptor((HandlerInterceptor)interceptor)
					.addPathPatterns(annotation.pathPatterns())
					.excludePathPatterns(getExclusionPathPattens())
					.order(annotation.order());
			String[] excludePathPatterns = annotation.excludePathPatterns();
			if(excludePathPatterns!=null&&excludePathPatterns.length>0)
				registration.excludePathPatterns(excludePathPatterns);
		}
	}

	private String[] getExclusionPathPattens() {
		if(EXCLUSION_PATH_PATTERN_S==null) {
			synchronized(FIXED_EXCLUSION_PATH_PATTERN_S) {
				if(EXCLUSION_PATH_PATTERN_S==null) {
					List<String> list = new ArrayList<>(FIXED_EXCLUSION_PATH_PATTERN_S.length+(flexibleExclusionUris==null?0:flexibleExclusionUris.length));
					for(String uriPatten:FIXED_EXCLUSION_PATH_PATTERN_S)
						list.add(uriPatten);
					if(flexibleExclusionUris!=null&&flexibleExclusionUris.length>0) {
						for(String _uriPatten:flexibleExclusionUris) {
							String uriPatten = _uriPatten.trim();
							if(uriPatten.length()>0)
								list.add(uriPatten);
						}
					}
					EXCLUSION_PATH_PATTERN_S = list.toArray(new String[] {});
				}
			}
		}
		return EXCLUSION_PATH_PATTERN_S;
	}
/*
	private final static String SWAGGER = "classpath:/META-INF/resources/webjars/swagger-ui/4.15.5/index.html";

	@Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
		registry.addResourceHandler(org.springdoc.core.utils.Constants.SWAGGER_UI_URL).addResourceLocations(SWAGGER);
		registry.addResourceHandler("swagger-ui.html").addResourceLocations(SWAGGER);
    }
*/
    @Autowired
    private freemarker.template.Configuration freemarkerCfg;

    @PostConstruct
    public void freeMarkerConfigurer() {
    	freemarkerCfg.setSharedVariable("attr", new AttributeTemplateDirectiveModelBean());
		freemarkerCfg.setSharedVariable("i18n", new I18NTemplateDirectiveModelBean(applicationContext.getEnvironment()));
		freemarkerCfg.setSharedVariable("property", new PropertiesTemplateDirectiveModelBean(applicationContext.getEnvironment()));
		freemarkerCfg.setSharedVariable("locale", new LocaleTemplateDirectiveModelBean());
    }
   
    public static void main(String[] args) {
    	List<String> list = new ArrayList<>();
    	list.add("aaa");
    	list.add("bbb");
    	list.add("ccc");
    	list.add("ddd");
    	list.add("eee");
    	String[] ss = list.toArray(new String[] {"111", "222", "333"});
    	for(String s:ss) {
    		System.out.println("S---------------"+s);
    	}
    }
}