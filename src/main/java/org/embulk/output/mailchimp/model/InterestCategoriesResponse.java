package org.embulk.output.mailchimp.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * Created by thangnc on 5/8/17.
 */
public class InterestCategoriesResponse
{
    @JsonProperty("categories")
    private List<CategoriesResponse> categories;

    public List<CategoriesResponse> getCategories()
    {
        return categories;
    }

    public void setCategories(List<CategoriesResponse> categories)
    {
        this.categories = categories;
    }
}
