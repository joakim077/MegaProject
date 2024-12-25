# Writing Tests for Your Spring Boot Application

This guide will help you write tests for your Java Spring Boot application and set up everything needed for running SonarQube analysis.

---

## 1. Project Setup for Testing

### a. Dependencies

Ensure your `pom.xml` includes the following dependencies for testing:

```xml
<dependencies>
    <!-- Spring Boot Starter Test -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-test</artifactId>
        <scope>test</scope>
    </dependency>
    
    <!-- Mockito for mocking dependencies -->
    <dependency>
        <groupId>org.mockito</groupId>
        <artifactId>mockito-core</artifactId>
        <scope>test</scope>
    </dependency>

    <!-- AssertJ for fluent assertions -->
    <dependency>
        <groupId>org.assertj</groupId>
        <artifactId>assertj-core</artifactId>
        <scope>test</scope>
    </dependency>

    <!-- H2 Database for integration testing -->
    <dependency>
        <groupId>com.h2database</groupId>
        <artifactId>h2</artifactId>
        <scope>test</scope>
    </dependency>
</dependencies>
```

### b. Directory Structure

Place your test classes in the `src/test/java` directory, mirroring the package structure of your main application. For example:

```plaintext
src/main/java/com/example/bankapp/
    service/AccountService.java
    controller/BankController.java
    repository/AccountRepository.java

src/test/java/com/example/bankapp/
    service/AccountServiceTest.java
    controller/BankControllerTest.java
    repository/AccountRepositoryTest.java
```

---

## 2. Writing Unit Tests

### a. Tests for `AccountService`

```java
package com.example.bankapp.service;

import com.example.bankapp.model.Account;
import com.example.bankapp.model.Transaction;
import com.example.bankapp.repository.AccountRepository;
import com.example.bankapp.repository.TransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class AccountServiceTest {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AccountService accountService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testRegisterAccount_Success() {
        String username = "testuser";
        String password = "password";

        when(accountRepository.findByUsername(username)).thenReturn(Optional.empty());
        when(passwordEncoder.encode(password)).thenReturn("encodedPassword");

        Account savedAccount = new Account();
        savedAccount.setUsername(username);
        savedAccount.setPassword("encodedPassword");
        savedAccount.setBalance(BigDecimal.ZERO);

        when(accountRepository.save(any(Account.class))).thenReturn(savedAccount);

        Account result = accountService.registerAccount(username, password);

        assertThat(result.getUsername()).isEqualTo(username);
        assertThat(result.getPassword()).isEqualTo("encodedPassword");
        assertThat(result.getBalance()).isEqualByComparingTo(BigDecimal.ZERO);
    }

    @Test
    void testRegisterAccount_UsernameExists() {
        String username = "testuser";
        String password = "password";

        when(accountRepository.findByUsername(username)).thenReturn(Optional.of(new Account()));

        assertThrows(RuntimeException.class, () -> accountService.registerAccount(username, password));
    }

    @Test
    void testDeposit() {
        Account account = new Account();
        account.setBalance(BigDecimal.valueOf(100));

        BigDecimal amount = BigDecimal.valueOf(50);

        accountService.deposit(account, amount);

        assertThat(account.getBalance()).isEqualByComparingTo(BigDecimal.valueOf(150));
        verify(accountRepository, times(1)).save(account);
        verify(transactionRepository, times(1)).save(any(Transaction.class));
    }

    @Test
    void testWithdraw_Success() {
        Account account = new Account();
        account.setBalance(BigDecimal.valueOf(100));

        BigDecimal amount = BigDecimal.valueOf(50);

        accountService.withdraw(account, amount);

        assertThat(account.getBalance()).isEqualByComparingTo(BigDecimal.valueOf(50));
        verify(accountRepository, times(1)).save(account);
        verify(transactionRepository, times(1)).save(any(Transaction.class));
    }

    @Test
    void testWithdraw_InsufficientFunds() {
        Account account = new Account();
        account.setBalance(BigDecimal.valueOf(50));

        BigDecimal amount = BigDecimal.valueOf(100);

        assertThrows(RuntimeException.class, () -> accountService.withdraw(account, amount));
    }

    @Test
    void testTransferAmount_Success() {
        Account fromAccount = new Account();
        fromAccount.setBalance(BigDecimal.valueOf(100));
        fromAccount.setUsername("sender");

        Account toAccount = new Account();
        toAccount.setBalance(BigDecimal.valueOf(50));
        toAccount.setUsername("receiver");

        when(accountRepository.findByUsername("receiver")).thenReturn(Optional.of(toAccount));

        accountService.transferAmount(fromAccount, "receiver", BigDecimal.valueOf(50));

        assertThat(fromAccount.getBalance()).isEqualByComparingTo(BigDecimal.valueOf(50));
        assertThat(toAccount.getBalance()).isEqualByComparingTo(BigDecimal.valueOf(100));

        verify(accountRepository, times(1)).save(fromAccount);
        verify(accountRepository, times(1)).save(toAccount);
        verify(transactionRepository, times(2)).save(any(Transaction.class));
    }
}
```

### b. Tests for `BankController`

```java
package com.example.bankapp.controller;

import com.example.bankapp.model.Account;
import com.example.bankapp.service.AccountService;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.ui.Model;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class BankControllerTest {

    @Mock
    private AccountService accountService;

    @Mock
    private Model model;

    @InjectMocks
    private BankController bankController;

    @Test
    void testRegisterAccount_Success() {
        String username = "testuser";
        String password = "password";

        String result = bankController.registerAccount(username, password, model);

        assertThat(result).isEqualTo("redirect:/login");
        verify(accountService, times(1)).registerAccount(username, password);
    }

    @Test
    void testRegisterAccount_Failure() {
        String username = "testuser";
        String password = "password";

        doThrow(new RuntimeException("Username already exists"))
                .when(accountService).registerAccount(username, password);

        String result = bankController.registerAccount(username, password, model);

        assertThat(result).isEqualTo("register");
        verify(model, times(1)).addAttribute(eq("error"), eq("Username already exists"));
    }

    @Test
    void testDeposit() {
        Account account = new Account();
        account.setUsername("testuser");
        account.setBalance(BigDecimal.valueOf(100));

        BigDecimal amount = BigDecimal.valueOf(50);

        when(accountService.findAccountByUsername("testuser")).thenReturn(account);

        String result = bankController.deposit(amount);

        assertThat(result).isEqualTo("redirect:/dashboard");
        verify(accountService, times(1)).deposit(account, amount);
    }
}
```

---

## 3. Configuring `application.properties` for Testing

Create a `src/test/resources/application.properties` file with the following configuration:

```properties
# Use H2 in-memory database for tests
spring.datasource.url=jdbc:h2:mem:testdb
spring.datasource.driverClassName=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=
spring.jpa.database-platform=org.hibernate.dialect.H2Dialect
spring.jpa.hibernate.ddl-auto=create-drop
```

---

## 4. Running Tests and Generating Reports

### Run Tests

To execute the tests, use:

```bash
mvn clean test
```

### Generate Code Coverage Report

To generate a code coverage report with JaCoCo:

```bash
mvn clean verify
```

This will produce a report at `target/site/jacoco/index.html`.

---

## 5. SonarQube Integration

Ensure your `pom.xml` is configured for SonarQube analysis:

```xml
<properties>
    <sonar.projectKey>com.example:bankapp</sonar.projectKey>
    <sonar.host.url>http://localhost:9000</sonar.host.url>
    <sonar.login>your_sonarqube_token</sonar.login>
</properties>
```
