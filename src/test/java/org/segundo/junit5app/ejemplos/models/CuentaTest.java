package org.segundo.junit5app.ejemplos.models;

import org.junit.jupiter.api.*;
import org.segundo.junit5app.ejemplos.exceptions.DineroInsuficienteException;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

//@TestInstance(TestInstance.Lifecycle.PER_CLASS) //Se puede realiza pero no es recomendable
class CuentaTest {

    Cuenta cuenta;

    @BeforeEach      //Se ejecuta antes de cada metodo test
    void initMethodTest() {
        this.cuenta = new Cuenta("Cesar", new BigDecimal("1000.12345"));
        System.out.println("Iniciando el método");
    }

    @AfterEach     //Se ejecuta despues de cada metodo test
    void tearDown() {
        System.out.println("Finalizando el método de prueba");
    }

    @BeforeAll  //Se ejecuta una sola vez, al incio, y este metodo por ser static pertenece a la clase
    static void beforeAll() {
        System.out.println("Inicializando el Test");
    }

    @AfterAll  //Se ejecuta una sola vez, al final, y este metodo por ser static pertenece a la clase., cerrar recursos
    static void afterAll() {
        System.out.println("Finalizando el Test");
    }

    @Test
    @DisplayName("Probando el nombre de la cuenta")
    void testNombreCuenta() {
        //cuenta.setPersona("Cesar");

        String esperado = "Cesar";
        String real = cuenta.getPersona();
        assertNotNull(real, () -> "La cuenta no puede ser nula");
        assertEquals(esperado, real, () -> "El nombre de la cuenta no es el que se esperaba");
        assertTrue(real.equals(esperado), () -> "Nombre de la cuenta esperada debe ser igual a la real");
    }

    @Test
    void testSaldoCuenta() {
        assertNotNull(cuenta.getSaldo());
        assertEquals(1000.12345, cuenta.getSaldo().doubleValue());
        assertFalse(cuenta.getSaldo().compareTo(BigDecimal.ZERO) < 0);
        assertTrue(cuenta.getSaldo().compareTo(BigDecimal.ZERO) > 0);
    }

    @Test
    void testReferenciaCuenta() {
        Cuenta cuenta = new Cuenta("Jhon Doe", new BigDecimal("8900.9997"));
        Cuenta cuenta2 = new Cuenta("Jhon Doe", new BigDecimal("8900.9997"));

        //assertNotEquals(cuenta2, cuenta);
        assertEquals(cuenta2, cuenta);
    }

    @Test
    void testDebitoCuenta() {

        cuenta.debito(new BigDecimal("100"));

        assertNotNull(cuenta.getSaldo());
        assertEquals(900, cuenta.getSaldo().intValue());
        assertEquals("900.12345", cuenta.getSaldo().toPlainString());
    }

    @Test
    void testCreditCuenta() {

        cuenta.credito(new BigDecimal("100"));

        assertNotNull(cuenta.getSaldo());
        assertEquals(1100, cuenta.getSaldo().intValue());
        assertEquals("1100.12345", cuenta.getSaldo().toPlainString());
    }

    @Test
    void testDineroInsuficienteException() {

        Exception exception = assertThrows(DineroInsuficienteException.class, () -> {
            cuenta.debito(new BigDecimal(1500));
        });

        String actual = exception.getMessage();
        String esperado = "Dinero Insuficiente";
        assertEquals(esperado, actual);
    }

    @Test
    //@Disabled
    @DisplayName("Testeando transferir dinero entre cuentas")
    void testTrasferirDineroCuentas() {
        //fail();
        Cuenta cuenta = new Cuenta("Cesar", new BigDecimal("2500"));
        Cuenta cuenta2 = new Cuenta("Juan", new BigDecimal("1500.8989"));

        Banco banco = new Banco();
        banco.setNombre("Banco del estado");
        banco.transferir(cuenta2, cuenta, new BigDecimal(500));

        assertEquals("1000.8989", cuenta2.getSaldo().toPlainString());
        assertEquals("3000", cuenta.getSaldo().toPlainString());
    }

    @Test
    @DisplayName("Probando relaciones entre las cuentas y banco con AssertAll")
    void testRelacionBancoCuenta() {
        Cuenta cuenta = new Cuenta("Cesar", new BigDecimal("2500"));
        Cuenta cuenta2 = new Cuenta("Juan", new BigDecimal("1500.8989"));

        Banco banco = new Banco();
        banco.addCuenta(cuenta);
        banco.addCuenta(cuenta2);

        banco.setNombre("Banco del estado");
        banco.transferir(cuenta2, cuenta, new BigDecimal(500));

        assertAll(
                () -> assertEquals("1000.8989", cuenta2.getSaldo().toPlainString(),
                        () -> "El valor del saldo de la cuenta2 no es el esperado"),
                () -> assertEquals("3000", cuenta.getSaldo().toPlainString(),
                        () -> "El valor del saldo de la cuenta1 no es el esperado"),
                () -> assertEquals(2, banco.getCuentas().size(),
                        () -> "El banco no tiene las cuentas esperadas"),
                () -> assertEquals("Banco del estado", cuenta.getBanco().getNombre(),
                        () -> "El nombre del banco no son iguales"),
                () -> assertEquals("Cesar", banco.getCuentas().stream()
                        .filter(c -> c.getPersona().equals("Cesar"))
                        .findFirst()
                        .get().getPersona(),
                        () -> "No se ha encontrado el valor por el cual se realizo la busqueda"
                ),
                () -> assertTrue(banco.getCuentas().stream().anyMatch(c -> c.getPersona().equals("Cesar")),
                        () -> "No existe una coicidencia con el valor proporcionado")
        );
    }
}