import com.thoughtworks.gauge.Step;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.FormBody;
import okhttp3.RequestBody;

import java.util.HashSet;
import org.junit.Assert;

import static org.assertj.core.api.Assertions.assertThat;

public class EmailSignUpStepImpl {
    final OkHttpClient client = new OkHttpClient();

    @Step("Post email address, <address>")
    public void postEmailAddress(String email) throws Exception {
        RequestBody form = new FormBody.Builder()
            .add("email", email).build();
        Request request = new Request.Builder()
            .url("http://localhost:8080/signup/email")
            .post(form).build();
        try(Response response = client.newCall(request).execute()) {
            assertThat(response.isSuccessful()).isTrue();
        }
    }

    @Step("Receive email with an one-time token")
    public void checkEmailAndSaveToken() {
        Assert.fail("implement me");
    }

    @Step("Post <password> as password and the one-time token")
    public void postPasswordAndOtp(String password) {
        Assert.fail("implement me");
    }

    @Step("Get successful response")
    public void assertSuccessResponse() {
        Assert.fail("implement me");
    }

}