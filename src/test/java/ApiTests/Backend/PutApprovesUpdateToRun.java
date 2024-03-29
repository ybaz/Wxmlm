package ApiTests.Backend;

import ApiTests.ObjectClasses.Approve;
import ApiTests.UsedByAll.DateForAPI;
import ApiTests.UsedByAll.MakeRequest;
import UsedByAll.CsvUsersReader;
import UsedByAll.TestUser;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.util.Calendar;
import java.util.Collection;

import static org.junit.Assert.*;

// * Created for W-xmlm by Fill on 06.03.2015.
@RunWith(value = Parameterized.class)
public class PutApprovesUpdateToRun {
    private TestUser testUser;

    @Parameterized.Parameters
    public static Collection testData() {
        return CsvUsersReader.getDataForTest("_BackendAPITest(");
    }

    public PutApprovesUpdateToRun(TestUser user){
        this.testUser = user;
    }

    @Test
    public void testPutApprovesUpdate() throws Exception {
        String siteUrl = UsedByAll.Config.getConfig().getProtocol() + UsedByAll.Config.getConfig().getScheme(); // Урл проверяемого сайта
        long startTime;
        long elapsedTime;
        Approve originalOne = new GetApprovesToRun(testUser).getAnyApprove(siteUrl);
        int statusValue = originalOne.getStatus() - 1;
        String approveUserIdValue = originalOne.getApproveUserId().toString();
        if(statusValue == -1){
            statusValue = 2;
        }
        if(approveUserIdValue.equals("")){
            approveUserIdValue = "1";
        }
        Approve modifiedOne = new Approve(originalOne.getId(), originalOne.getUserId(), 1, originalOne.getCreateDate(), originalOne.getUpdateDate(), DateForAPI.makeDateTimeString(Calendar.getInstance(), 0), statusValue, originalOne.getDocuments());
        String originalJson = "[{\"id\": " + originalOne.getId() + ", \"user_id\": " + originalOne.getUserId() + ", \"approve_user_id\": " + approveUserIdValue + ", \"create_date\": \"" + originalOne.getCreateDate() + "\", \"approve_date\": \"" + originalOne.getApproveDate() + "\", \"status\": " + originalOne.getStatus() + "}]";
        String modifiedJson = "[{\"id\": " + modifiedOne.getId() + ", \"user_id\": " + modifiedOne.getUserId() + ", \"approve_user_id\": " + modifiedOne.getApproveUserId() + ", \"create_date\": \"" + modifiedOne.getCreateDate() + "\", \"approve_date\": \"" + modifiedOne.getApproveDate() + "\", \"status\": " + modifiedOne.getStatus() + "}]";
        // Содзаем URL
        startTime = System.currentTimeMillis();
        HttpURLConnection httpCon = MakeRequest.getConnection(siteUrl, testUser, "users/api/approves/update/", "PUT", "application/json", "application/json", true);
        OutputStreamWriter out = new OutputStreamWriter(httpCon.getOutputStream());
        out.write(modifiedJson);
        out.close();
        assertTrue("Check response code is 200", httpCon.getResponseCode() == 200);
        elapsedTime = System.currentTimeMillis() - startTime;
        // Проверяем GET-запросом, что данные обновились
        Approve changedOne = new GetApprovesToRun(testUser).getApproveByParameter("id", originalOne.getId(), siteUrl);
        assertTrue("Check modified data saved correctly", modifiedOne.equalsExceptUpdatedDateAndApproveUserId(changedOne, true));

        // Содзаем URL
        httpCon = MakeRequest.getConnection(siteUrl, testUser, "users/api/approves/update/", "PUT", "application/json", "application/json", true);
        out = new OutputStreamWriter(httpCon.getOutputStream());
        out.write(originalJson);
        out.close();
        httpCon.getInputStream();
        assertTrue("Check response code is 200", httpCon.getResponseCode() == 200);

        // Проверяем GET-запросом, что данные восстановились

        changedOne = new GetApprovesToRun(testUser).getApproveByParameter("id", originalOne.getId(), siteUrl);

        assertTrue("Check modified data returned correctly", originalOne.equalsExceptUpdatedDateAndApproveUserId(changedOne, true));
        System.out.println("Total elapsed http request/response time in milliseconds: " + elapsedTime);
    }
}
