import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.thoughtworks.gauge.Step;
import com.thoughtworks.gauge.datastore.DataStoreFactory;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.FormBody;
import okhttp3.RequestBody;

import org.assertj.core.util.Strings;
import org.junit.Assert;

import static org.assertj.core.api.Assertions.assertThat;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class EmailSignUpStepImpl {
    final OkHttpClient client = new OkHttpClient();

    @Step("Post email address, <address>")
    public void postEmailAddress(String address) throws Exception {
        putScenarioData("email", address);
        RequestBody form = new FormBody.Builder()
            .add("email", address).build();
        Request request = new Request.Builder()
            .url("http://localhost:8080/signup/email")
            .post(form).build();
        try(Response response = client.newCall(request).execute()) {
            assertThat(response.isSuccessful()).isTrue();
            JsonElement element = new JsonParser().parse(response.body().string());
            String id = element.getAsJsonObject().get("ID").getAsString();
            assertThat(id).isNotNull();
            String ref = element.getAsJsonObject().get("HREF").getAsString();
            assertThat(ref).isNotNull();
            putScenarioData("ref", ref);
        }
    }

    @Step("Receive email with an one-time token")
    public void checkEmailAndSaveToken() throws Exception {
        String emailPath = System.getenv("email_dir");
        String email = (String)getScenarioData("email");
        if (emailPath == null || emailPath.isEmpty()) {
            throw new RuntimeException("Require email_path to be set on environment properties file.");
        }
        List<String> s = Files.readAllLines(Paths.get(emailPath, email));
        String token = s.get(0);
        if (token.isEmpty()) {
            throw new RuntimeException("Missing otp token in file.");
        }
        putScenarioData("code", token);
    }

    @Step("Post <password> as password and the one-time token")
    public void postPasswordAndOtp(String password) throws Exception {
        String code = (String)getScenarioData("code");
        String ref = (String)getScenarioData("ref");
        
        RequestBody form = new FormBody.Builder()
            .add("code", code)
            .add("password", password)
            .add("password_confirm", password)
            .build();

        String url = Strings.join("http://localhost:8080/signup", ref).with("/");
        Request request = new Request.Builder().url(url).put(form).build();
        try(Response response = client.newCall(request).execute()) {
            assertThat(response.isSuccessful()).isTrue();
        }
    }

    @Step("Get successful response and a redirect to login")
    public void assertSuccessResponse() {
        Assert.fail("implement me");
    }

    private Object getScenarioData(String key) {
        return DataStoreFactory.getScenarioDataStore().get(key);
    }

    private void putScenarioData(String key, Object data) {
        DataStoreFactory.getScenarioDataStore().put(key, data);
    }

}