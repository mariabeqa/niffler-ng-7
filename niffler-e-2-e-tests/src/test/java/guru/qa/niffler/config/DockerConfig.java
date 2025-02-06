package guru.qa.niffler.config;

import com.codeborne.selenide.Configuration;

enum DockerConfig implements Config {
  INSTANCE;

  static {
    Configuration.timeout = 8000;
  }

  @Override
  public String frontUrl() {
    return "";
  }

  @Override
  public String authUrl() {
    return "";
  }

  @Override
  public String authJdbcUrl() {
    return "";
  }

  @Override
  public String gatewayUrl() {
    return "";
  }

  @Override
  public String userdataUrl() {
    return "";
  }

  @Override
  public String userdataJdbcUrl() {
    return "";
  }

  @Override
  public String spendUrl() {
    return "";
  }

  @Override
  public String spendJdbcUrl() {
    return "";
  }

  @Override
  public String currencyJdbcUrl() {
    return "";
  }

  @Override
  public String ghUrl() {
    return "";
  }
}
