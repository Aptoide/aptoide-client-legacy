package com.aptoide.amethyst.webservices;

import com.aptoide.amethyst.preferences.SecurePreferences;
import com.aptoide.dataprovider.webservices.json.GenericResponseV2;
import com.aptoide.dataprovider.webservices.models.WebserviceOptions;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.octo.android.robospice.request.retrofit.RetrofitSpiceRequest;

import java.util.ArrayList;
import java.util.List;

import retrofit.RetrofitError;
import retrofit.http.Body;
import retrofit.http.POST;

/**
 * Created by fabio on 22-10-2015.
 */
public class MakeReviewRequest extends RetrofitSpiceRequest<GenericResponseV2, MakeReviewRequest.Webservice> {

    ReviewPost reviewPost;

    public void setPackage_name(String package_name) {
        reviewPost.set_package(package_name);
    }
    public void setRepoName(String repoID) {
        reviewPost.setRepo_name(repoID);
    }
    public void addLocale(ReviewPost.Locale locale) {
        reviewPost.getLocales().add(locale);
    }
    public void setPerformance(int p) {
        reviewPost.setPerformance(p);
    }
    public void setStability(int p) {
        reviewPost.setStability(p);
    }
    public void setUsability(int p) {
        reviewPost.setUsability(p);
    }
    public void setAddiction(int p) {
        reviewPost.setAddiction(p);
    }

    public interface Webservice{
        @POST("/www.aptoide.com/webservices/3/setReview")
        GenericResponseV2 makeReview(@Body ReviewPost review);
    }

    public static class ReviewPost{

        @JsonProperty("package")
        private String _package;
        private String repo_name;
        private String access_token;
        private String mode;
        private String status;
        private int performance;
        private int stability;
        private int usability;
        private int addiction;

        private List<Locale> locales = new ArrayList<>();

        public static class Locale{
            private String lang_id;
            @JsonProperty("final_verdict")
            private String finalVerdict;
            private List<String> cons ;
            private List<String> pros ;

            public Locale(String lang_id){
                this.lang_id = lang_id;
            }

            public String getLang_id() {
                return lang_id;
            }
            public String getFinalVerdict() {
                return finalVerdict;
            }

            public void setFinalVerdict(String finalVerdict) {
                this.finalVerdict = finalVerdict;
            }

            public List<String> getPros() {
                return pros;
            }

            public void setPros(List<String> pros) {
                this.pros = pros;
            }
            public List<String> getCons() {
                return cons;
            }

            public void setCons(List<String> cons) {
                this.cons = cons;
            }
        }

        public List<Locale> getLocales() {
            return locales;
        }

        public void setLocales(List<Locale> locales) {
            this.locales = locales;
        }
        public String getMode() {
            return mode;
        }

        public void setMode(String mode) {
            this.mode = mode;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getRepo_name() {
            return repo_name;
        }

        public void setRepo_name(String repo_name) {
            this.repo_name = repo_name;
        }



        public int getPerformance() {
            return performance;
        }

        public void setPerformance(int performance) {
            this.performance = performance;
        }

        public int getStability() {
            return stability;
        }

        public void setStability(int stability) {
            this.stability = stability;
        }

        public int getUsability() {
            return usability;
        }

        public void setUsability(int usability) {
            this.usability = usability;
        }

        public int getAddiction() {
            return addiction;
        }

        public void setAddiction(int addiction) {
            this.addiction = addiction;
        }
        public String getAccess_token() {
            return access_token;
        }

        public void setAccess_token(String access_token) {
            this.access_token = access_token;
        }

        public String get_package() {
            return _package;
        }

        public void set_package(String _package) {
            this._package = _package;
        }
    }

    String baseUrl = WebserviceOptions.WebServicesLink + "3/setReview";

    public MakeReviewRequest() {
        super(GenericResponseV2.class, Webservice.class);
        reviewPost = new ReviewPost();

    }

    @Override
    public GenericResponseV2 loadDataFromNetwork() throws Exception {

        reviewPost.setAccess_token(SecurePreferences.getInstance().getString("access_token", "empty"));
        reviewPost.setMode("json");
        reviewPost.setStatus("active");
        //AptoideUtils.getMyCountryCode(context)

        GenericResponseV2 responseV2 = null;

        try{
            responseV2 = getService().makeReview(reviewPost);
        }catch (RetrofitError error){
            OauthErrorHandler.handle(error);
        }
        return responseV2;
    }

}
