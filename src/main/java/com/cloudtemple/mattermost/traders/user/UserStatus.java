package com.cloudtemple.mattermost.traders.user;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;

/**
 * Represents a User Status
 */
public class UserStatus
{
  public String userId;
  public String status;
  public boolean manual = false;
  public long lastActivityAt;
  private Map<String, Object> data = new HashMap<String, Object>();

  public String getUserId()
  {
    return userId;
  }
  public void setUserId(String userId) {
    this.userId = userId;
  }

  public String getStatus()
  {
    return status;
  }
  public void setStatus(String status) {
    this.status = status;
  }

  public boolean isManual()
  {
    return manual;
  }

  public void setManual(final boolean manual)
  {
    this.manual = manual;
  }


  public long getLastActivityAt()
  {
    return lastActivityAt;
  }
  public void setLastActivityAt(long lastActivityAt) {
    this.lastActivityAt = lastActivityAt;
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

