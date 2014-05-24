package es.ubu.inf.tfg.doc.datos;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import es.ubu.inf.tfg.doc.datos.Traductor;
import es.ubu.inf.tfg.doc.datos.TraductorHTML;
import es.ubu.inf.tfg.regex.asu.AhoSethiUllman;
import es.ubu.inf.tfg.regex.thompson.ConstruccionSubconjuntos;

public class TraductorHTMLTest {

	Traductor traductor;

	@Before
	public void setUp() throws Exception {
		traductor = new TraductorHTML();
	}

	@After
	public void tearDown() throws Exception {
		traductor = null;
	}

	/**
	 * Comprueba la correcta generación de un documento que contenga los datos
	 * dados.
	 */
	@Test
	public void testDocumento() {
		String esperado = toString("TraductorVacio.html");

		assertEquals("Generación incorrecta de documento HTML.", esperado,
				traductor.documento(new ArrayList<String>()));
	}

	/**
	 * Comprueba la correcta traducción de un problema de tipo Aho-Sethi-Ullman
	 * subtipo construcción.
	 */
	@Test
	public void testTraduceAhoSethiUllmanConstruccion() {
		// TODO
		fail();
	}

	/**
	 * Comprueba la correcta traducción de un problema de tipo Aho-Sethi-Ullman
	 * subtipo etiquetado.
	 */
	@Test
	public void testTraduceAhoSethiUllmanEtiquetado() {
		AhoSethiUllman problema = new AhoSethiUllman("((a|b*)a*c)*");
		String esperado = toString("TraductorASUEtiquetado.html");
		String encontrado = traductor.traduceASUEtiquetado(problema);

		encontrado = encontrado
				.replaceAll("<img src=\".*\">", "<img src=\"\">");

		assertEquals(
				"Traducción HTML incorrecta de problema AhoSethiUllman subtipo etiquetado.",
				esperado, encontrado);
	}

	/**
	 * Comprueba la correcta traducción de un problema de tipo Aho-Sethi-Ullman
	 * subtipo tablas.
	 */
	@Test
	public void testTraduceAhoSethiUllmanTablas() {
		AhoSethiUllman problema = new AhoSethiUllman("((a|b*)a*c)*");
		String esperado = toString("TraductorASUTablas.html");

		assertEquals(
				"Traducción HTML incorrecta de problema AhoSethiUllman subtipo tablas.",
				esperado, traductor.traduceASUTablas(problema));
	}

	/**
	 * Comprueba la correcta traducción de un problema de construcción de
	 * subconjuntos subtipo expresion.
	 */
	@Test
	public void testTraduceConstruccionSubconjuntosExpresion() {
		ConstruccionSubconjuntos problema = new ConstruccionSubconjuntos(
				"((a|b*)a*c)*");
		String esperado = toString("TraductorCSExpresion.html");

		assertEquals(
				"Traducción HTML incorrecta de problema de construcción de subconjuntos subtipo expresión.",
				esperado, traductor.traduceCSExpresion(problema));
	}

	/**
	 * Comprueba la correcta traducción de un problema de construcción de
	 * subconjuntos subtipo autómata.
	 */
	@Test
	public void testTraduceConstruccionSubconjuntosAutomata() {
		ConstruccionSubconjuntos problema = new ConstruccionSubconjuntos(
				"((a|b*)a*c)*");
		String esperado = toString("TraductorCSAutomata.html");
		String encontrado = traductor.traduceCSAutomata(problema);

		encontrado = encontrado
				.replaceAll("<img src=\".*\">", "<img src=\"\">");

		assertEquals(
				"Traducción HTML incorrecta de problema de construcción de subconjuntos subtipo autómata.",
				esperado, encontrado);
	}

	/**
	 * Lee un recurso como una cadena de caracteres.
	 * 
	 * @param fichero
	 *            Recurso a leer.
	 * @return Contenido del recurso.
	 */
	private String toString(String fichero) {
		String resultado;
		StringBuilder contenido;
		String linea;

		try (InputStream entrada = getClass().getResourceAsStream(fichero);
				BufferedReader lector = new BufferedReader(
						new InputStreamReader(entrada, "UTF8"))) {

			contenido = new StringBuilder();
			linea = lector.readLine();
			while (linea != null) {
				contenido.append(linea);
				linea = lector.readLine();
				if (linea != null)
					contenido.append("\n");
			}

			resultado = contenido.toString();
			return resultado;
		} catch (IOException e) {
			fail("Error al abrir el archivo " + fichero);
			return "";
		}
	}
}
