package ApiTests.Backend;

import ApiTests.UsedByAll.MakeRequest;
import ApiTests.UsedByAll.ValidationChecker;
import UsedByAll.TestUser;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Created by User on 12/11/2014.
 */
public class GetDocuments {
    static final String url = "users/api/documents/";
    @Test
    public boolean testGetDocuments(String scheme, TestUser testUser) throws IOException, JSONException {

        HttpURLConnection httpCon = MakeRequest.getConnection(scheme, testUser, url, 5, "GET");
        InputStream inStrm = httpCon.getInputStream();
        assertTrue("Check response code is 200", httpCon.getResponseCode() == 200);
        InputStreamReader isReader = new InputStreamReader(inStrm);
        BufferedReader br = new BufferedReader(isReader);
        String result = "";
        String line;
        while ((line = br.readLine()) != null) {
            result += line;
        }
        br.close();

        //JSON
        JSONArray jsonArr = new JSONArray(result);
        //Structure
        assertNotNull("Получен пустой массив. Проверить метод с наличием объектов.", jsonArr.length());
        for(int i = 0; i<jsonArr.length(); i++)
        {
            JSONObject object = jsonArr.getJSONObject(i);
            assertTrue("Incorrect id", ValidationChecker.checkIdValue(object.getInt("id")));
            assertTrue("Incorrect user_id", ValidationChecker.checkIdValue(object.getInt("user_id")));
            assertTrue("Incorrect file_name", ValidationChecker.checkFileName(object.get("file_name").toString()));
            assertTrue("Incorrect path", ValidationChecker.checkStringNotNull(object.getString("path")));
            assertTrue("Incorrect created_date", ValidationChecker.checkDateTimeString(object.getString("created_date")));
            assertTrue("Incorrect updated_date", ValidationChecker.checkDateTimeString(object.getString("updated_date")));
            assertTrue("Incorrect status", ValidationChecker.checkBooleanValue(object.getBoolean("status")));
            assertTrue("Incorrect approve_id", ValidationChecker.checkIdOrNull(object.get("approve_id")));
            assertTrue("Incorrect md5", ValidationChecker.checkStringNotNull("md5"));

            assertEquals("Incorrect count of JSON objects", object.length(), 9);
        }
        return true;

    }
    public static int[] getDocumentsId(String scheme, TestUser testUser) throws IOException, JSONException {
        HttpURLConnection httpCon = MakeRequest.getConnection(scheme, testUser, url, 5, "GET");
        InputStream inStrm = httpCon.getInputStream();
        assertTrue("Check response code is 200", httpCon.getResponseCode() == 200);
        InputStreamReader isReader = new InputStreamReader(inStrm);
        BufferedReader br = new BufferedReader(isReader);
        String result = "";
        String line;
        while ((line = br.readLine()) != null) {
            result += line;
        }
        br.close();

        JSONArray jsonArr = new JSONArray(result);
        int ids[] = new int[jsonArr.length()];
        assertNotNull("Получен пустой массив. Проверить метод с наличием объектов.", jsonArr.length());
        for(int i=0; i<jsonArr.length(); i++){
            JSONObject object = jsonArr.getJSONObject(i);
            ids[i] = object.getInt("id");
        }
        return ids;
    }
}