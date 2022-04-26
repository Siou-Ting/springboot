package com.example.demo;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
//import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import net.sf.json.JSONObject;

//@Controller
//public class mainController {
//	
//	@ResponseBody
//	@GetMapping("/")
//	public String hello() {
//		return "Hello World Tomcat!";
//	}  
//}

@Controller
public class mainController {

	private static final Logger log = LoggerFactory.getLogger(mainController.class);

	@Value("${spring.application.name}")
	String appName; //配合application.properties中的spring.application.name，和template/。

	//此頁單純印字串
	@GetMapping("/test")
	public String homePage(Model model) {
		model.addAttribute("appName", appName);
		return "home";
	}

//    @Configuration
//    @EnableWebSecurity
//    public class SecurityConfig extends WebSecurityConfigurerAdapter {
//    	@Override
//        protected void configure(HttpSecurity http) throws Exception {
//            http.authorizeRequests()
//                .anyRequest()
//                .permitAll()
//                .and().csrf().disable();
//        }
//    }

	
//  將json請求帶的資料解析過，送到前端（html）。
//	@ResponseBody  //打開註解:顯示結果。
	@RequestMapping(value = "/request/test", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
	public String test(HttpServletRequest request, Model model) {
		
		String line;
		StringBuilder sb = new StringBuilder();
		ObjectMapper objectMapper = new ObjectMapper();

		try {
			while ((line = request.getReader().readLine()) != null) {
				sb.append(line);
			}

			log.info(sb.toString());

			Map recvMap = objectMapper.readValue(sb.toString(), new TypeReference<Map>() {});
//			System.out.print("oooooooooooooooooooo"+recvMap);

			model.addAttribute("name", String.valueOf(recvMap.get("name")));
			model.addAttribute("gender", String.valueOf(recvMap.get("gender")));
			model.addAttribute("age", String.valueOf(recvMap.get("age")));

		} catch (Exception e) {
			log.error(e.getMessage());
			for (StackTraceElement elem : e.getStackTrace()) {
				log.error(elem.toString());
			}
		}
		return "homeShowJsonRequestAttribute";
	}

	
	
// 將json請求帶的資料解析過抓特定key return原始資料。
	@ResponseBody  
	@RequestMapping(value = "/request/data", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
	public String getByRequest(HttpServletRequest request) {
		
		// 得到JSONObject
		JSONObject jsonParam = this.getJSONParam(request, null);

		// 將得到的JSON物件進行封裝後return
		JSONObject result = new JSONObject();
		result.put("msg", "ok");
		result.put("method", "request");
		result.put("data", jsonParam);
		//System.out.println("呆呆: " + jsonParam.get("name"));

		return result.toString();
	}
	
	public JSONObject getJSONParam(HttpServletRequest request, Model model) {    //jsonParam.get("<KEY>")可以取到特定值。
		JSONObject jsonParam = null;
		try {
			// get json data
			BufferedReader streamReader = new BufferedReader(new InputStreamReader(request.getInputStream(), "UTF-8"));

			// write data
			StringBuilder sb = new StringBuilder();
			String line = null;
			while ((line = streamReader.readLine()) != null) {
				sb.append(line);
			}
			jsonParam = JSONObject.fromObject(sb.toString());
			// print JSON data
//			System.out.println(jsonParam.toString());  //印出請求的Json資料

			// get specific JSON Key-value
			jsonParam.getJSONObject("data");
//			String name = jsonParam.getString("name");  //json值轉字串變數
//			String gender = jsonParam.getString("gender");
//			String age = jsonParam.getString("age");
//			System.out.println("Output: name = " + name);
			model.addAttribute("name", jsonParam.get("name")); //將這些json中的值被model做資源管理，/template/home.html就可以顯示這些特定值。
			model.addAttribute("gender", jsonParam.get("gender"));
			model.addAttribute("age", jsonParam.get("age"));

		} catch (Exception e) {
			e.printStackTrace();}
		
		return jsonParam;
		
	}
}
