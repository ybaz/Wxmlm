package ApiTests.LoginFree;

import ApiTests.Backend.GetProductsToRun;
import ApiTests.UsedByAll.MakeRequest;
import ApiTests.UsedByAll.ValidationChecker;
import UsedByAll.RegionMatch;
import UsedByAll.TestUser;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

//* Created for W-xmlm by Fill on 05.12.2014. Gets module's current version by product_id
public class GetModuleInfo {
    public boolean testGetModuleInfo(String siteUrl, TestUser user) throws Exception {
        long startTime;
        long elapsedTime;
        int[] ids = GetProductsToRun.getProductsIDs(siteUrl, user);

        for (int i = 0; i < (ids.length - 1); i++) {
            // Создаем соединение
            startTime = System.currentTimeMillis();
            HttpURLConnection httpCon = MakeRequest.getConnection(siteUrl, "application/api/desktop/get-module-info/?product_id=" + ids[i], "GET");

            // Отправляем запрос
            int responseCode = httpCon.getResponseCode();
            assertTrue("Check response code is 200", (responseCode == 404 || responseCode == 200));
            InputStream inStrm;
            try {
                inStrm = httpCon.getInputStream();
            }
            catch (FileNotFoundException e){
                inStrm = httpCon.getErrorStream();
            }
            elapsedTime = System.currentTimeMillis() - startTime;
            // Читаем ответ
            String result = "";
            BufferedReader br;
            InputStreamReader isReader = new InputStreamReader(inStrm);
            br = new BufferedReader(isReader);
            String line;
            while ((line = br.readLine()) != null) {
                result += line;
            }
            br.close();
            if (RegionMatch.IsStringRegionMatch(result, "<br />")) {
                System.out.println("Response contains html in its body. Look: " + result);
                return false;
            }
            //Парсим JSON
            JSONObject object = new JSONObject(result);
            if (object.has("errors")) {
                assertTrue("Error is different from \"Application not found\"", object.getString("errors").equals("Application not found"));
                System.out.println("Product with ID " + ids[i] + ": application not found");
            }
            else if (object.has("error")) {
                assertTrue("Error is different from \"Application not found\"", object.getString("error").equals("Application not found"));
                System.out.println("Product with ID " + ids[i] + ": application not found");
            }
            else {
                //Проверяем структуру
                assertTrue("Incorrect id", ValidationChecker.checkIdValue(object.getInt("id")));
                assertTrue("Incorrect product_id", (ValidationChecker.checkIdValue(object.getInt("product_id"))) && (ids[i] == object.getInt("product_id")));
                assertTrue("Incorrect title", ValidationChecker.checkStringOrNull(object.getString("title")));
                assertTrue("Incorrect version", ValidationChecker.checkStringNotNull(object.getString("version")));
                assertTrue("Incorrect description", ValidationChecker.checkStringOrNull(object.getString("description")));
                assertTrue("Incorrect filename", ValidationChecker.checkStringNotNull(object.getString("filename")));
                assertTrue("Incorrect url", ValidationChecker.checkURLOnDomain(object.getString("url"), siteUrl));
                assertEquals("Incorrect count of Json parameters", object.length(), 7);
            }
            System.out.println("Total elapsed http request/response time in milliseconds: " + elapsedTime + " by product_id = " + i);
        }
        return true;
    }
}