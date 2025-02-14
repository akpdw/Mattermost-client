package com.cloudtemple.mattermost.traders.user;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;

/**
 * Represents a User Status
 */
public class CustomStatus
{
  public String emoji;
  public String text;
  private Map<String, Object> data = new HashMap<String, Object>();

  public String getEmoji()
  {
    return emoji;
  }
  public void setEmoji(String emoji) {
    this.emoji = emoji;
  }

  public String getText()
  {
    return text;
  }
  public void setText(String text) {
    this.text = text;
  }

  @JsonAnyGetter
  public Map<String, Object> getData() {
    return data;
  }

  @JsonAnySetter
  public void setData(String name, Object value) {
    this.data.put(name, value);
  }



}

