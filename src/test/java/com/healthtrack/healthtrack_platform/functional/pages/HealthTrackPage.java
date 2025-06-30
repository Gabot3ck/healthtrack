package com.healthtrack.healthtrack_platform.functional.pages;

import org.openqa.selenium.*;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.time.Duration;

/**
 * Page Object Model para la página principal de HealthTrack
 * Encapsula la interacción con los elementos de la interfaz
 */
public class HealthTrackPage {
    
    private final WebDriver driver;
    private final WebDriverWait wait;
    
    // ===== ELEMENTOS DE REGISTRO =====
    @FindBy(css = "#nombre")
    private WebElement nombreInput;
    
    @FindBy(css = "#peso-inicial")
    private WebElement pesoInicialInput;
    
    @FindBy(css = "#btn-registrar")
    private WebElement btnRegistrar;
    
    @FindBy(css = "#registration-form")
    private WebElement registrationForm;
    
    // ===== ELEMENTOS DEL DASHBOARD =====
    @FindBy(css = "#user-dashboard")
    private WebElement userDashboard;
    
    @FindBy(css = "#usuario-nombre")
    private WebElement usuarioNombre;
    
    @FindBy(css = "#peso-actual")
    private WebElement pesoActual;
    
    @FindBy(css = "#ultima-actualizacion")
    private WebElement ultimaActualizacion;
    
    @FindBy(css = "#nuevo-peso")
    private WebElement nuevoPesoInput;
    
    @FindBy(css = "#btn-actualizar")
    private WebElement btnActualizar;
    
    @FindBy(css = "#btn-nuevo-usuario")
    private WebElement btnNuevoUsuario;
    
    // ===== ELEMENTOS DE MENSAJES =====
    @FindBy(css = "#error-message")
    private WebElement errorMessage;
    
    @FindBy(css = "#success-message")
    private WebElement successMessage;
    
    @FindBy(css = "#warning-message")
    private WebElement warningMessage;
    
    // ===== OTROS ELEMENTOS =====
    @FindBy(tagName = "h1")
    private WebElement pageTitle;
    
    public HealthTrackPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        PageFactory.initElements(driver, this);
    }
    
    // ===== MÉTODOS DE NAVEGACIÓN =====
    
    /**
     * Navega a la página principal
     */
    public HealthTrackPage navigateTo(String url) {
        driver.get(url);
        waitForPageLoad();
        return this;
    }
    
    /**
     * Espera a que la página se cargue completamente
     */
    public HealthTrackPage waitForPageLoad() {
        wait.until(ExpectedConditions.visibilityOf(pageTitle));
        return this;
    }
    
    // ===== MÉTODOS DE REGISTRO =====
    
    /**
     * Registra un nuevo usuario
     */
    public HealthTrackPage registrarUsuario(String nombre, String peso) {
        waitForElement(nombreInput);
        nombreInput.clear();
        nombreInput.sendKeys(nombre);
        
        pesoInicialInput.clear();
        pesoInicialInput.sendKeys(peso);
        
        btnRegistrar.click();
        return this;
    }
    
    /**
     * Registra usuario usando Enter
     */
    public HealthTrackPage registrarUsuarioConEnter(String nombre, String peso) {
        waitForElement(nombreInput);
        nombreInput.clear();
        nombreInput.sendKeys(nombre);
        
        pesoInicialInput.clear();
        pesoInicialInput.sendKeys(peso);
        pesoInicialInput.sendKeys(Keys.ENTER);
        
        return this;
    }
    
    // ===== MÉTODOS DE ACTUALIZACIÓN =====
    
    /**
     * Actualiza el peso del usuario
     */
    public HealthTrackPage actualizarPeso(String peso) {
        waitForElement(nuevoPesoInput);
        nuevoPesoInput.clear();
        nuevoPesoInput.sendKeys(peso);
        btnActualizar.click();
        return this;
    }
    
    /**
     * Actualiza peso usando Enter
     */
    public HealthTrackPage actualizarPesoConEnter(String peso) {
        waitForElement(nuevoPesoInput);
        nuevoPesoInput.clear();
        nuevoPesoInput.sendKeys(peso);
        nuevoPesoInput.sendKeys(Keys.ENTER);
        return this;
    }
    
    // ===== MÉTODOS DE NAVEGACIÓN EN LA APP =====
    
    /**
     * Hace click en "Nuevo Usuario"
     */
    public HealthTrackPage clickNuevoUsuario() {
        btnNuevoUsuario.click();
        waitForElement(registrationForm);
        return this;
    }
    
    // ===== MÉTODOS DE VERIFICACIÓN =====
    
    /**
     * Verifica si el formulario de registro está visible
     */
    public boolean isRegistrationFormVisible() {
        return isElementVisible(registrationForm);
    }
    
    /**
     * Verifica si el dashboard está visible
     */
    public boolean isDashboardVisible() {
        return isElementVisible(userDashboard);
    }
    
    /**
     * Obtiene el nombre del usuario mostrado
     */
    public String getUsuarioNombre() {
        return usuarioNombre.getText();
    }
    
    /**
     * Obtiene el peso actual mostrado
     */
    public String getPesoActual() {
        return pesoActual.getText();
    }
    
    /**
     * Obtiene el texto de última actualización
     */
    public String getUltimaActualizacion() {
        return ultimaActualizacion.getText();
    }
    
    /**
     * Obtiene el título de la página
     */
    public String getPageTitle() {
        return pageTitle.getText();
    }
    
    /**
     * Obtiene el título del navegador
     */
    public String getBrowserTitle() {
        return driver.getTitle();
    }
    
    // ===== MÉTODOS DE MENSAJES =====
    
    /**
     * Verifica si hay un mensaje de error visible
     */
    public boolean isErrorMessageVisible() {
        return isElementVisible(errorMessage) && !errorMessage.getText().isEmpty();
    }
    
    /**
     * Verifica si hay un mensaje de éxito visible
     */
    public boolean isSuccessMessageVisible() {
        return isElementVisible(successMessage) && !successMessage.getText().isEmpty();
    }
    
    /**
     * Verifica si hay un mensaje de advertencia visible
     */
    public boolean isWarningMessageVisible() {
        return isElementVisible(warningMessage) && !warningMessage.getText().isEmpty();
    }
    
    /**
     * Obtiene el texto del mensaje de error
     */
    public String getErrorMessage() {
        waitForMessage(errorMessage);
        return errorMessage.getText();
    }
    
    /**
     * Obtiene el texto del mensaje de éxito
     */
    public String getSuccessMessage() {
        waitForMessage(successMessage);
        return successMessage.getText();
    }
    
    /**
     * Obtiene el texto del mensaje de advertencia
     */
    public String getWarningMessage() {
        waitForMessage(warningMessage);
        return warningMessage.getText();
    }
    
    // ===== MÉTODOS DE ESTADO DEL BOTÓN =====
    
    /**
     * Verifica si el botón de actualizar está habilitado
     */
    public boolean isActualizarButtonEnabled() {
        return btnActualizar.isEnabled();
    }
    
    /**
     * Obtiene el texto del botón de actualizar
     */
    public String getActualizarButtonText() {
        return btnActualizar.getText();
    }
    
    // ===== MÉTODOS DE VALIDACIÓN DE CAMPOS =====
    
    /**
     * Obtiene el valor del campo nombre
     */
    public String getNombreValue() {
        return nombreInput.getAttribute("value");
    }
    
    /**
     * Obtiene el valor del campo peso inicial
     */
    public String getPesoInicialValue() {
        return pesoInicialInput.getAttribute("value");
    }
    
    /**
     * Obtiene el valor del campo nuevo peso
     */
    public String getNuevoPesoValue() {
        return nuevoPesoInput.getAttribute("value");
    }
    
    /**
     * Verifica si los campos están limpios después de reset
     */
    public boolean areFieldsClean() {
        return getNombreValue().isEmpty() && 
               getPesoInicialValue().isEmpty() && 
               getNuevoPesoValue().isEmpty();
    }
    
    // ===== MÉTODOS AUXILIARES PRIVADOS =====
    
    private boolean isElementVisible(WebElement element) {
        try {
            return element.isDisplayed();
        } catch (NoSuchElementException | StaleElementReferenceException e) {
            return false;
        }
    }
    
    private void waitForElement(WebElement element) {
        wait.until(ExpectedConditions.visibilityOf(element));
    }
    
    private void waitForMessage(WebElement messageElement) {
        wait.until(ExpectedConditions.visibilityOf(messageElement));
    }
    
    /**
     * Espera hasta que el dashboard sea visible
     */
    public HealthTrackPage waitForDashboard() {
        wait.until(ExpectedConditions.visibilityOf(userDashboard));
        return this;
    }
    
    /**
     * Espera hasta que el formulario de registro sea visible
     */
    public HealthTrackPage waitForRegistrationForm() {
        wait.until(ExpectedConditions.visibilityOf(registrationForm));
        return this;
    }
    
    /**
     * Espera hasta que aparezca un mensaje de éxito
     */
    public HealthTrackPage waitForSuccessMessage() {
        wait.until(ExpectedConditions.visibilityOf(successMessage));
        return this;
    }
    
    /**
     * Espera hasta que aparezca un mensaje de error
     */
    public HealthTrackPage waitForErrorMessage() {
        wait.until(ExpectedConditions.visibilityOf(errorMessage));
        return this;
    }
}