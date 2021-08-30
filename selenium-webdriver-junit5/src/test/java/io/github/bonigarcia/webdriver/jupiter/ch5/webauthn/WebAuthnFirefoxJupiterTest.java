/*
 * (C) Copyright 2021 Boni Garcia (https://bonigarcia.github.io/)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package io.github.bonigarcia.webdriver.jupiter.ch5.webauthn;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.Duration;
import java.util.UUID;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.UnsupportedCommandException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.virtualauthenticator.HasVirtualAuthenticator;
import org.openqa.selenium.virtualauthenticator.VirtualAuthenticator;
import org.openqa.selenium.virtualauthenticator.VirtualAuthenticatorOptions;

import io.github.bonigarcia.wdm.WebDriverManager;

class WebAuthnFirefoxJupiterTest {

    WebDriver driver;

    @BeforeEach
    void setup() {
        driver = WebDriverManager.firefoxdriver().create();
    }

    @AfterEach
    void teardown() {
        driver.quit();
    }

    @Test
    void testWebAuthn() {
        assertThatThrownBy(() -> {
            driver.get("https://bonigarcia.dev/selenium-webdriver-java/");
            HasVirtualAuthenticator virtualAuthenticator = (HasVirtualAuthenticator) driver;
            WebDriverWait wait = new WebDriverWait(driver,
                    Duration.ofSeconds(10));

            WebElement link = driver
                    .findElement(By.linkText("Web authentication"));
            wait.until(ExpectedConditions.elementToBeClickable(link));
            link.click();

            VirtualAuthenticatorOptions authOptions = new VirtualAuthenticatorOptions();
            VirtualAuthenticator authenticator = virtualAuthenticator
                    .addVirtualAuthenticator(authOptions);

            String randomId = UUID.randomUUID().toString();
            driver.findElement(By.id("input-email")).sendKeys(randomId);
            driver.findElement(By.id("register-button")).click();
            wait.until(ExpectedConditions.textToBePresentInElementLocated(
                    By.className("popover-body"),
                    "Success! Now try logging in"));

            driver.findElement(By.id("login-button")).click();
            wait.until(ExpectedConditions.textToBePresentInElementLocated(
                    By.className("main-content"), "You're logged in!"));

            virtualAuthenticator.removeVirtualAuthenticator(authenticator);
        }).isInstanceOf(UnsupportedCommandException.class);
    }

}