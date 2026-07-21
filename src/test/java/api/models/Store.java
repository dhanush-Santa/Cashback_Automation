package api.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;

import java.util.List;

/**
 * Maps to a single object inside data.data[] of GET /data/stores.
 * ignoreUnknown = true so new fields added by the API later don't break deserialization.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Store {

    private String name;
    private String about;

    @JsonProperty("terms_todo")
    private String termsTodo;

    @JsonProperty("terms_not_todo")
    private String termsNotTodo;

    private String tips;

    @JsonProperty("tracking_speed")
    private String trackingSpeed;

    private Integer id;
    private String slug;
    private String logo;

    @JsonProperty("banner_image")
    private String bannerImage;

    private String homepage;

    @JsonProperty("domain_name")
    private String domainName;

    @JsonProperty("extension_afflink")
    private String extensionAfflink;

    private List<String> cats;

    @JsonProperty("cashback_enabled")
    private Integer cashbackEnabled;

    @JsonProperty("cashback_amount")
    private String cashbackAmount;

    @JsonProperty("cashback_type")
    private String cashbackType;

    @JsonProperty("cashback_was")
    private String cashbackWas;

    @JsonProperty("amount_type")
    private String amountType;

    @JsonProperty("rate_type")
    private String rateType;

    @JsonProperty("confirm_duration")
    private String confirmDuration;

    @JsonProperty("is_claimable")
    private Boolean isClaimable;

    @JsonProperty("is_featured")
    private Boolean isFeatured;

    @JsonProperty("is_promoted")
    private Boolean isPromoted;

    private Integer visits;

    @JsonProperty("offers_count")
    private Integer offersCount;

    private String rating;

    @JsonProperty("rating_count")
    private Integer ratingCount;

    private Integer clicks;
    private Integer ghost;
    private String filter;

    @JsonProperty("apply_coupon")
    private String applyCoupon;

    @JsonProperty("checkout_url")
    private String checkoutUrl;

    private List<String> countries;

    @JsonProperty("lock_country")
    private Integer lockCountry;

    private String alpha;

    @JsonProperty("cashback_sort")
    private String cashbackSort;

   @JsonProperty("similar_stores")
private JsonNode similarStores;

    @JsonProperty("cashback_string")
    private String cashbackString;

    @JsonProperty("confirm_days")
    private String confirmDays;

    public Store() {
        // required by Jackson
    }

    // ---------- Getters & setters ----------

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getAbout() { return about; }
    public void setAbout(String about) { this.about = about; }

    public String getTermsTodo() { return termsTodo; }
    public void setTermsTodo(String termsTodo) { this.termsTodo = termsTodo; }

    public String getTermsNotTodo() { return termsNotTodo; }
    public void setTermsNotTodo(String termsNotTodo) { this.termsNotTodo = termsNotTodo; }

    public String getTips() { return tips; }
    public void setTips(String tips) { this.tips = tips; }

    public String getTrackingSpeed() { return trackingSpeed; }
    public void setTrackingSpeed(String trackingSpeed) { this.trackingSpeed = trackingSpeed; }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getSlug() { return slug; }
    public void setSlug(String slug) { this.slug = slug; }

    public String getLogo() { return logo; }
    public void setLogo(String logo) { this.logo = logo; }

    public String getBannerImage() { return bannerImage; }
    public void setBannerImage(String bannerImage) { this.bannerImage = bannerImage; }

    public String getHomepage() { return homepage; }
    public void setHomepage(String homepage) { this.homepage = homepage; }

    public String getDomainName() { return domainName; }
    public void setDomainName(String domainName) { this.domainName = domainName; }

    public String getExtensionAfflink() { return extensionAfflink; }
    public void setExtensionAfflink(String extensionAfflink) { this.extensionAfflink = extensionAfflink; }

    public List<String> getCats() { return cats; }
    public void setCats(List<String> cats) { this.cats = cats; }

    public Integer getCashbackEnabled() { return cashbackEnabled; }
    public void setCashbackEnabled(Integer cashbackEnabled) { this.cashbackEnabled = cashbackEnabled; }

    public String getCashbackAmount() { return cashbackAmount; }
    public void setCashbackAmount(String cashbackAmount) { this.cashbackAmount = cashbackAmount; }

    public String getCashbackType() { return cashbackType; }
    public void setCashbackType(String cashbackType) { this.cashbackType = cashbackType; }

    public String getCashbackWas() { return cashbackWas; }
    public void setCashbackWas(String cashbackWas) { this.cashbackWas = cashbackWas; }

    public String getAmountType() { return amountType; }
    public void setAmountType(String amountType) { this.amountType = amountType; }

    public String getRateType() { return rateType; }
    public void setRateType(String rateType) { this.rateType = rateType; }

    public String getConfirmDuration() { return confirmDuration; }
    public void setConfirmDuration(String confirmDuration) { this.confirmDuration = confirmDuration; }

    public Boolean getIsClaimable() { return isClaimable; }
    public void setIsClaimable(Boolean isClaimable) { this.isClaimable = isClaimable; }

    public Boolean getIsFeatured() { return isFeatured; }
    public void setIsFeatured(Boolean isFeatured) { this.isFeatured = isFeatured; }

    public Boolean getIsPromoted() { return isPromoted; }
    public void setIsPromoted(Boolean isPromoted) { this.isPromoted = isPromoted; }

    public Integer getVisits() { return visits; }
    public void setVisits(Integer visits) { this.visits = visits; }

    public Integer getOffersCount() { return offersCount; }
    public void setOffersCount(Integer offersCount) { this.offersCount = offersCount; }

    public String getRating() { return rating; }
    public void setRating(String rating) { this.rating = rating; }

    public Integer getRatingCount() { return ratingCount; }
    public void setRatingCount(Integer ratingCount) { this.ratingCount = ratingCount; }

    public Integer getClicks() { return clicks; }
    public void setClicks(Integer clicks) { this.clicks = clicks; }

    public Integer getGhost() { return ghost; }
    public void setGhost(Integer ghost) { this.ghost = ghost; }

    public String getFilter() { return filter; }
    public void setFilter(String filter) { this.filter = filter; }

    public String getApplyCoupon() { return applyCoupon; }
    public void setApplyCoupon(String applyCoupon) { this.applyCoupon = applyCoupon; }

    public String getCheckoutUrl() { return checkoutUrl; }
    public void setCheckoutUrl(String checkoutUrl) { this.checkoutUrl = checkoutUrl; }

    public List<String> getCountries() { return countries; }
    public void setCountries(List<String> countries) { this.countries = countries; }

    public Integer getLockCountry() { return lockCountry; }
    public void setLockCountry(Integer lockCountry) { this.lockCountry = lockCountry; }

    public String getAlpha() { return alpha; }
    public void setAlpha(String alpha) { this.alpha = alpha; }

    public String getCashbackSort() { return cashbackSort; }
    public void setCashbackSort(String cashbackSort) { this.cashbackSort = cashbackSort; }

    public JsonNode getSimilarStores() { return similarStores; }
    public void setSimilarStores(JsonNode similarStores) { this.similarStores = similarStores; }

    public String getCashbackString() { return cashbackString; }
    public void setCashbackString(String cashbackString) { this.cashbackString = cashbackString; }

    public String getConfirmDays() { return confirmDays; }
    public void setConfirmDays(String confirmDays) { this.confirmDays = confirmDays; }

    @Override
    public String toString() {
        return "Store{id=" + id + ", name='" + name + "', slug='" + slug + "', cashbackAmount='" + cashbackAmount + "'}";
    }
}