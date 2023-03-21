package com.example.customcamundatasklist.util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class JsonToHtml {

	// Constructor
	public JsonToHtml() {

	}

	public String getHtml(String strJsonData) throws JSONException {

		// System.out.println(new JSONObject(strJsonData).get("components").getClass());
		return jsonToHtml(new JSONObject(strJsonData).get("components"));
		// return "Hello world";
	}

	/**
	 * convert json Data to structured Html text
	 * 
	 * @param json
	 * @return string
	 * @throws JSONException
	 */
	private String jsonToHtml(Object obj) throws JSONException {
		StringBuilder html = new StringBuilder();

		// html.append("<!DOCTYPE html> <html xmlns:th=\"http://www.thymeleaf.org\">");

		String label, input;

		JSONArray array = (JSONArray) obj;
		System.out.println("json array length:" + array.length());
		for (int i = 0; i < array.length(); i++) {
			JSONObject component = (JSONObject) array.get(i);
			System.out.println(component.get("type"));
			// System.out.println(array.get(i));
			label = componentToLabel(component);
			input = componentToInput(component);
			html.append(label);
			html.append(input);
		}

		// html.append("</html>");

		return html.toString();
	}

	private String componentToLabel(JSONObject component) throws JSONException {
		String label = "";

		if (component.get("type").equals("text")) {
			label = "<label>" + component.get("text") + "</label><br>";
		} else {
			label = "<label>" + component.get("label") + "</label><br>";
		}

		return label;
	}

	private String componentToInput(JSONObject component) throws JSONException {
		String input = "";

		String componentType = component.get("type").toString();

		switch (componentType) {
		case "textfield":
			input = "<input type=\"textfield\" name=" + component.get("key").toString() + "><br>";
			break;
		case "text":
			break; // text only show text in label, no input element
		case "number":
			input = "<input type=\"number\" name=" + component.get("key").toString() + "><br>";
			break;
		case "radio":
			// loop to create input for each radio buttons
			JSONArray array = (JSONArray) component.get("values");
			for (int i = 0; i < array.length(); i++) {
				JSONObject c = (JSONObject) array.get(i);
				input += "<input type=\"radio\" name=" + component.get("key") + " value=\"" + c.get("value") + "\">";
				input += "<label>" + c.get("label") + "</label>";
			}
			break;
		default:
			System.out.println("Not supported component type: " + component.get("type"));
			input = "<p>ERROR COMPONENT</p><br>";
			break;
		}

		return input;
	}
}