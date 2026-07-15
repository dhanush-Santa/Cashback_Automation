package api.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * Maps to a single object inside data[] of GET /data/categories.
 * Each category carries its own nested list of Store objects (which include
 * a parent_category_id back-reference to this category).
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Category {

    private String name;
    private String description;
    private Integer id;

    @JsonProperty("parent_id")
    private Integer parentId;

    private String icon;

    @JsonProperty("is_featured")
    private Integer isFeatured;

    @JsonProperty("icon_class")
    private String iconClass;

    private List<Store> stores;

    // Recursive - categories can nest sub-categories (empty in current sample data).
    private List<Category> children;

    public Category() {
        // required by Jackson
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public Integer getParentId() { return parentId; }
    public void setParentId(Integer parentId) { this.parentId = parentId; }

    public String getIcon() { return icon; }
    public void setIcon(String icon) { this.icon = icon; }

    public Integer getIsFeatured() { return isFeatured; }
    public void setIsFeatured(Integer isFeatured) { this.isFeatured = isFeatured; }

    public String getIconClass() { return iconClass; }
    public void setIconClass(String iconClass) { this.iconClass = iconClass; }

    public List<Store> getStores() { return stores; }
    public void setStores(List<Store> stores) { this.stores = stores; }

    public List<Category> getChildren() { return children; }
    public void setChildren(List<Category> children) { this.children = children; }

    @Override
    public String toString() {
        int storeCount = stores == null ? 0 : stores.size();
        return "Category{id=" + id + ", name='" + name + "', stores=" + storeCount + "}";
    }
}