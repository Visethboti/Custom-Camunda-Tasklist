package com.example.customcamundatasklist.service;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

@Service
public class FormJsonToHtmlImpl implements FormJsonToHtml {

	@Override
	public String getHtml(String strJsonData) throws JSONException {
		return jsonToHtml(new JSONObject(strJsonData).get("components"));
	}

	private String jsonToHtml(Object obj) throws JSONException {
		StringBuilder html = new StringBuilder();
		String input;

		JSONArray array = (JSONArray) obj;
		for (int i = 0; i < array.length(); i++) {
			JSONObject component = (JSONObject) array.get(i);
			input = componentToInput(component);
			html.append(input);
		}

		return html.toString();
	}

	private String componentToInput(JSONObject component) throws JSONException {
		String input = "";

		String componentType = component.get("type").toString();

		switch (componentType) {
		case "textfield":
			input += "<label>" + component.get("label") + "</label><br>";
			input += "<input type=\"textfield\" name=" + component.get("key").toString() + "><br>";
			break;
		case "text":
			input += "<label>" + component.get("text") + "</label><br>";
			break; // text only show text in label, no input element
		case "number":
			input = "<label>" + component.get("label") + "</label><br>";
			input += "<input type=\"number\" name=" + component.get("key").toString() + "><br>";
			break;
		case "radio":
			// loop to create input for each radio buttons
			input += "<label>" + component.get("label") + "</label><br>";
			JSONArray array = (JSONArray) component.get("values");
			for (int i = 0; i < array.length(); i++) {
				JSONObject c = (JSONObject) array.get(i);
				input += "<input type=\"radio\" name=" + component.get("key") + " value=\"" + c.get("value") + "\">";
				input += "<label>" + c.get("label") + "</label>";
			}
			break;
		case "checkbox":
			input += "<input type=\"checkbox\" name=" + component.get("key") + "\">";
			input += "<label>" + component.get("label") + "</label><br>";
			break;
		case "checklist": // checklist currently do not work properly, it need to send an array of values
							// instead of one value
			// loop to create input for each radio buttons
			input += "<label>" + component.get("label") + "</label><br>";
			JSONArray array1 = (JSONArray) component.get("values");
			for (int i = 0; i < array1.length(); i++) {
				JSONObject c = (JSONObject) array1.get(i);
				input += "<input type=\"checkbox\" name=\"" + component.get("key") + "[]\" value=\"" + c.get("value")
						+ "\">";
				input += "<label>" + c.get("label") + "</label>";
			}
			break;
		case "select":
			input += "<label>" + component.get("label") + "</label><br>";
			JSONArray array2 = (JSONArray) component.get("values");
			input += "<select name=\"" + component.get("key") + "\"required>";
			input += "<option value=\"\" selected disabled hidden>Choose here</option>";
			for (int i = 0; i < array2.length(); i++) {
				JSONObject c = (JSONObject) array2.get(i);
				input += "<option value=\"" + c.get("value") + "\">" + c.get("label") + "</option>";
			}
			input += "</select><br>";
			break;
		default:
			System.out.println("Not supported component type: " + component.get("type"));
			input = "<p>ERROR COMPONENT</p><br>";
			break;
		}

		return input;
	}
}