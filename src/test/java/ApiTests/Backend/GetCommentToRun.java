package ApiTests.Backend;

import ApiTests.ObjectClasses.Comment;
import ApiTests.UsedByAll.MakeRequest;
import ApiTests.UsedByAll.ValidationChecker;
import UsedByAll.Config;
import UsedByAll.CsvUsersReader;
import UsedByAll.TestUser;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.util.Collection;

import static org.junit.Assert.*;

/**
 * Created by User on 5/18/2015.
 */
@RunWith(value = Parameterized.class)
public class GetCommentToRun {
    private TestUser testUser;

    @Parameterized.Parameters
    public static Collection testDara() {return CsvUsersReader.getDataForTest("_BackendAPITest("); }

    public GetCommentToRun(TestUser user) {this.testUser = user; }

    @Test
    public void testGetComment() throws Exception {
        String siteUrl = Config.getConfig().getProtocol() + Config.getConfig().getScheme(); // Урл проверяемого сайта
        long startTime;
        long elapsedTime;
        startTime = System.currentTimeMillis();
        HttpURLConnection httpCon = MakeRequest.getConnection(siteUrl, testUser, "users/api/comment/", 500, "GET");
        InputStream inStrm = httpCon.getInputStream();
        assertTrue("Check response code is 200", httpCon.getResponseCode() == 200);
        elapsedTime = System.currentTimeMillis() - startTime;
        InputStreamReader isReader = new InputStreamReader(inStrm);
        BufferedReader br = new BufferedReader(isReader);
        String result = "";
        String line;
        while ((line = br.readLine()) != null) {
            result += line;
        }
        br.close();

        //Парсим JSON
        JSONArray jsonArr = new JSONArray(result);
        //Проверяем структуру
        assertNotSame("Получен пустой массив. Рекомендуется проверить метод с наличием объектов. ", jsonArr.length(), 0);
        for (int i = 0; i < jsonArr.length(); i++) {
            JSONObject object = jsonArr.getJSONObject(i);

            assertTrue("Incorrect id", ValidationChecker.checkIdValue(object.getInt("id")));
            assertTrue("Incorrect type_id", ValidationChecker.checkCommentType(object.getInt("type_id")));
            assertTrue("Incorrect text", ValidationChecker.checkStringNotNull(object.getString("text")));
            assertTrue("Incorrect user_id", ValidationChecker.checkIdOrNull(object.get("user_id")));
            assertTrue("Incorrect purchase_id", ValidationChecker.checkIdOrNull(object.get("purchase_id")));
            assertTrue("Incorrect operation_id", ValidationChecker.checkIdOrNull(object.get("operation_id")));
            assertTrue("Incorrect account_id", ValidationChecker.checkIdOrNull(object.get("account_id")));
            assertTrue("Incorrect recharge_request_id", ValidationChecker.checkIdOrNull(object.get("recharge_request_id")));
            assertTrue("Incorrect withdraw_request_id", ValidationChecker.checkIdOrNull(object.get("withdraw_request_id")));
            assertTrue("Incorrect verification_request_id", ValidationChecker.checkIdOrNull(object.get("verification_request_id")));
            assertTrue("Incorrect front_event_type_id", ValidationChecker.checkIdOrNull(object.get("front_event_type_id")));
            assertTrue("Incorrect back_event_type_id", ValidationChecker.checkIdOrNull(object.get("back_event_type_id")));
            assertTrue("Incorrect created_date", ValidationChecker.checkDateTimeString(object.getString("created_date")));
            assertTrue("Incorrect updated_date", ValidationChecker.checkDateTimeString(object.getString("updated_date")));
            assertEquals("Incorrect count of JSON parameters", object.length(), 14);


        }
        System.out.println("Total elapsed http request/response time in milliseconds: " + elapsedTime);

    }
    public Comment getAnyComment(String siteUrl) throws IOException {
        HttpURLConnection httpCon = MakeRequest.getConnection(siteUrl, testUser, "users/api/comment/", 500, "GET");
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
        try{
            JSONArray jsonArr = new JSONArray(result);
            assertFalse("There is an empty Array", jsonArr.length()==0);
            JSONObject object = jsonArr.getJSONObject(0);
            return new Comment(object.getInt("id"), object.getInt("type_id"), object.getString("text"), object.get("user_id"), object.get("purchase_id"), object.get("operation_id"), object.get("account_id"), object.get("recharge_request_id"), object.get("withdraw_request_id"), object.get("verification_request_id"), object.get("front_event_type_id"), object.get("back_event_type_id"), object.getString("created_date"), object.getString("updated_date"));

        }
        catch(Exception e){
            e.printStackTrace();
            return null;
        }
    }
    public Comment getCommentByParameter(String parameterName, int parameterValue, String siteUrl) throws IOException {
        HttpURLConnection httpCon = MakeRequest.getConnection(siteUrl, testUser, "users/api/comment/", 500, "GET");
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
        try{
            JSONArray jsonArr = new JSONArray(result);
            assertFalse("There is an empty Array", jsonArr.length()==0);
            for(int i=0; i<jsonArr.length(); i++){
                JSONObject object = jsonArr.getJSONObject(i);
                if (object.getInt(parameterName) == parameterValue) {
                    return new Comment(object.getInt("id"), object.getInt("type_id"), object.getString("text"), object.get("user_id"), object.get("purchase_id"), object.get("operation_id"), object.get("account_id"), object.get("recharge_request_id"), object.get("withdraw_request_id"), object.get("verification_request_id"), object.get("front_event_type_id"), object.get("back_event_type_id"), object.getString("created_date"), object.getString("updated_date"));
                }
            }

        }catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return null;
    }
}
