package ApiTests.Backend;

import ApiTests.ObjectClasses.Purchase;
import ApiTests.UsedByAll.MakeRequest;
import ApiTests.UsedByAll.ValidationChecker;
import UsedByAll.Config;
import UsedByAll.CsvUsersReader;
import UsedByAll.TestUser;
import org.json.JSONArray;
import org.json.JSONException;
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

@RunWith(value = Parameterized.class)
public class GetPurchasesToRun {
    private TestUser testUser;
    public String url = "products/api/purchase/";

    @Parameterized.Parameters
    public static Collection testData() {
        return CsvUsersReader.getDataForTest("_BackendAPITest(");
    }

    public GetPurchasesToRun(TestUser user){
        this.testUser = user;
    }

    @Test
    public void testGetPurchases() throws Exception{
        String siteUrl = Config.getConfig().getProtocol() + Config.getConfig().getScheme(); // Урл проверяемого сайта
        long startTime;
        long elapsedTime;
        startTime = System.currentTimeMillis();
        HttpURLConnection httpCon = MakeRequest.getConnection(siteUrl, testUser, url, 500, "GET");
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
        JSONArray jsonArr = new JSONArray(result);
        assertNotNull("Получен пустой массив. Проверить метод с наличием объектов.", jsonArr.length());

        for (int i=0; i<jsonArr.length(); i++){
            JSONObject object = jsonArr.getJSONObject(i);
            assertTrue("Incorrect id", ValidationChecker.checkIdValue(object.getInt("id")));
            assertTrue("Incorrect buyer_user_id", ValidationChecker.checkIdValue(object.getInt("buyer_user_id")));
            assertTrue("Incorrect product_id " + object.getInt("product_id"), ValidationChecker.checkProductId(object.getInt("product_id")));
            assertTrue("Incorrect date", ValidationChecker.checkDateTimeString(object.getString("date")));
            assertTrue("Incorrect updated_date", ValidationChecker.checkDateTimeString(object.getString("updated_date")));
            assertTrue("Incorrect price", ValidationChecker.checkMoneyFormat(object.get("price").toString()));
            assertTrue("Incorrect payment_amount", ValidationChecker.checkDoubleValue(object.getDouble("payment_amount")));
            assertTrue("Incorrect status", ValidationChecker.checkPurchaseStatusId(object.getInt("status")));
            assertTrue("Incorrect terms", ValidationChecker.checkStringOrNull(object.get("terms")));
            assertEquals("Incorrect count of JSON Objects", object.length(),9);
        }
        System.out.println("Total elapsed http request/response time in milliseconds: " + elapsedTime);
    }

    public Purchase getAnyPurchase(TestUser testUser, String siteUrl) throws IOException, JSONException {
        HttpURLConnection httpCon = MakeRequest.getConnection(siteUrl, testUser, url, 500, "GET");
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
        assertNotEquals("Получен пустой массив. Проверить метод с наличием объектов.", jsonArr.length(), 0);
        JSONObject object = jsonArr.getJSONObject(0);
        String terms = null;
        if(object.get("terms")!= null)
            terms = object.get("terms").toString();
        return new Purchase(object.getInt("id"), object.getInt("buyer_user_id"), object.getInt("product_id"), object.getString("date"),object.get("price").toString(),object.getDouble("payment_amount"), object.getInt("status"), terms);
    }

    public Purchase getPurchaseByParameter(String parameterName, int parameterValue, TestUser testUser, String siteUrl) throws IOException {
        HttpURLConnection httpCon = MakeRequest.getConnection(siteUrl, testUser, url, 100, "GET" );
        InputStream inStrm = httpCon.getInputStream();
        assertTrue("Check response code is 200", httpCon.getResponseCode() == 200);
        InputStreamReader isReader = new InputStreamReader(inStrm);
        BufferedReader br = new BufferedReader(isReader);
        String result = "";
        String line;
        while((line=br.readLine()) !=null) {
            result +=line;
        }
        br.close();
        try{
            JSONArray jsonArr = new JSONArray(result);
            for(int i=0; i<jsonArr.length(); i++){
                JSONObject object = jsonArr.getJSONObject(i);
                if(object.getInt(parameterName) == parameterValue){
                    String terms = null;
                    if(object.get("terms") != null)
                        terms = object.get("terms").toString();
                   // System.out.println(new Purchases(object.getInt("id"), object.getInt("buyer_user_id"), object.getInt("product_id"), object.getString("date"),object.get("price").toString(),object.getDouble("payment_amount"), object.getInt("status"), terms));
                    return new Purchase(object.getInt("id"), object.getInt("buyer_user_id"), object.getInt("product_id"), object.getString("date"),object.get("price").toString(),object.getDouble("payment_amount"), object.getInt("status"), terms);
                }
            }
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
        return null;
    }
}