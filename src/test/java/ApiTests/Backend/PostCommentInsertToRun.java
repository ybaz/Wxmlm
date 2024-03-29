package ApiTests.Backend;

import ApiTests.ObjectClasses.Comment;
import ApiTests.UsedByAll.MakeRequest;
import UsedByAll.Config;
import UsedByAll.CsvUsersReader;
import UsedByAll.RandomString;
import UsedByAll.TestUser;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.io.*;
import java.net.HttpURLConnection;
import java.util.Collection;

import static org.junit.Assert.assertTrue;

/**
 * Created by User on 5/19/2015.
 */
@RunWith(value = Parameterized.class)
public class PostCommentInsertToRun {
    private TestUser testUser;
    @Parameterized.Parameters
    public static Collection testData() {
        return CsvUsersReader.getDataForTest("_BackendAPITest(");
    }

    public PostCommentInsertToRun(TestUser user) {this.testUser=user; }

    @Test
    public void testPostCommentInsert() throws IOException, JSONException {
        String siteUrl = Config.getConfig().getProtocol() + Config.getConfig().getScheme(); // Урл проверяемого сайта
        long startTime;
        long elapsedTime;
        Comment originalOne = new GetCommentToRun(testUser).getAnyComment(siteUrl);
        Comment newOne = new Comment(originalOne.getId(), originalOne.getTypeId(), RandomString.generateString(6), originalOne.getUserId(), originalOne.getPurchaseId(), originalOne.getOperationId(), originalOne.getAccountId(), originalOne.getRechargeRequestId(), originalOne.getWithdrawRequestId(), originalOne.getVerificationRequestId(), originalOne.getFrontEventTypeId(), originalOne.getBackEventTypeId(), originalOne.getCreatedDate(), originalOne.getUpdatedDate());

        String newJson = "{\"type_id\": " + newOne.getTypeId() + ", \"text\": \"" + newOne.getText() + "\", \"user_id\": " + newOne.getUserId() + ", \"purchase_id\": " + newOne.getPurchaseId() + ", \"operation_id\": " + newOne.getOperationId() + ", \"account_id\": " + newOne.getAccountId() + ", \"recharge_request_id\": " + newOne.getRechargeRequestId() + ", \"withdraw_request_id\": " + newOne.getWithdrawRequestId() + ", \"verification_request_id\": " + newOne.getVerificationRequestId() + ", \"back_event_type_id\": " + newOne.getBackEventTypeId() + "}";
        System.out.println(newJson);

        // Содзаем URL
        startTime = System.currentTimeMillis();
        HttpURLConnection httpCon = MakeRequest.getConnection(siteUrl, testUser, "users/api/comment/insert/", "POST", "application/json", "application/json", true);
        OutputStreamWriter out = new OutputStreamWriter(httpCon.getOutputStream());
        out.write(newJson);
        out.close();
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
        // Берем из респонса id новой операции
        JSONObject response = new JSONObject(result);
        JSONArray reports = response.getJSONArray("reports");
        JSONObject report = reports.getJSONObject(0);
        newOne.setId(report.getInt("id"));

        // Проверяем GET-запросом, что данные обновились
        Comment changedOne = new GetCommentToRun(testUser).getCommentByParameter("id", newOne.getId(), siteUrl);
        assertTrue("Check modified data saved correctly", newOne.equalsExceptDates(changedOne, false, false));
        System.out.println("Total elapsed http request/response time in milliseconds: " + elapsedTime);

    }
}
