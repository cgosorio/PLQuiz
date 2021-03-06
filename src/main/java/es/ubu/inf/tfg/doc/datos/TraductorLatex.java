package es.ubu.inf.tfg.doc.datos;

import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import es.ubu.inf.tfg.regex.asu.AhoSethiUllman;
import es.ubu.inf.tfg.regex.thompson.ConstruccionSubconjuntos;

/**
 * Implementa un traductor Latex.
 * 
 * @author Roberto Izquierdo Amo
 * 
 */
public class TraductorLatex extends Traductor {

	private static final Logger log = LoggerFactory
			.getLogger(TraductorLatex.class);

	/**
	 * Genera un documento Latex a partir de una lista de problemas ya
	 * traducidos.
	 * 
	 * @param problemas
	 *            Lista de problemas traducidos.
	 * @return Documento Latex completo.
	 */
	@Override
	public String documento(List<Plantilla> problemas) {
		log.info("Generando documento Latex a partir de {} problemas.",
				problemas.size());

		StringBuilder documento = new StringBuilder();

		int n = 1;
		for (Plantilla problema : problemas) {
			problema.set("numero", "" + n++);
			documento.append(problema.toString());
		}

		Plantilla plantilla = new Plantilla("plantilla.tex");
		plantilla.set("documento", documento.toString());

		return plantilla.toString();
	}
	
	/**
	 * Genera un documento Latex a partir de un único problema ya traducido.
	 * 
	 * @param problema
	 *            Problema traducido.
	 * @return Documento Latex completo.
	 */
	@Override
	public String traduceProblema(Plantilla problema, int num){
		log.info("Generando documento Latex de un único problema.");
		
		problema.set("numero", "" + num);
		
		Plantilla plantilla = new Plantilla("plantilla.tex");
		plantilla.set("documento", problema.toString());
	
		return plantilla.toString();
	}

	/**
	 * Traduce un problema de tipo AhoSethiUllman subtipo construcción a formato
	 * Latex. En latex el problema de construcción consiste en dibujar el árbol,
	 * no en identificar el correcto.
	 * 
	 * @param problema
	 *            Problema AhoSethiUllman.
	 * @return Problema traducido a Latex.
	 */
	@Override
	public Plantilla traduceASUConstruccion(AhoSethiUllman problema) {
		log.info(
				"Traduciendo a Latex problema tipo Aho-Sethi-Ullman con expresión {}, formato construcción",
				problema.problema());

		Plantilla plantilla = new Plantilla("plantillaASUConstruccion.tex");
		String imagen = problema.alternativas().get(0).hashCode() + "";

		String expresion = problema.problema();
		expresion = expresion.replace("\u2027", "\\cdot ").replace("·", "\\cdot ").replace(".", "\\cdot ");
		expresion = expresion.replace("\u03B5", "\\epsilon ");
		expresion = expresion.replace("$", "\\$ ");
		expresion = expresion.replace("*", "^*");
		expresion = "$ " + expresion + " $";

		plantilla.set("expresion", expresion);
		plantilla.set("imagen", imagen);

		return plantilla;
	}

	/**
	 * Traduce un problema de tipo AhoSethiUllman subtipo etiquetado a formato
	 * Latex.
	 * 
	 * @param problema
	 *            Problema AhoSethiUllman.
	 * @return Problema traducido a Latex.
	 */
	@Override
	public Plantilla traduceASUEtiquetado(AhoSethiUllman problema) {
		log.info(
				"Traduciendo a Latex problema tipo Aho-Sethi-Ullman con expresión {}, formato etiquetado",
				problema.problema());

		String imagen = "" + problema.arbolVacio().hashCode();
		Plantilla plantilla = new Plantilla("plantillaASUEtiquetado.tex");
		StringBuilder soluciones = new StringBuilder();

		soluciones.append(" & anulable? & primera-pos & última-pos");
		soluciones.append("\\\\ \\hline\n");
		char simboloActual = 'A';
		while (problema.primeraPos(simboloActual) != null) {
			soluciones.append(simboloActual + " & ");
			soluciones
					.append((problema.esAnulable(simboloActual) ? "Si" : "No")
							+ " & ");
			soluciones.append(setToRanges(problema.primeraPos(simboloActual)) 
					+ " & ");
			soluciones.append(setToRanges(problema.ultimaPos(simboloActual))  
					+ "\\\\ \\hline\n");

			simboloActual++;
		}
		soluciones.append("\\end{tabular}");

		String solucionesL = soluciones.toString();
		solucionesL = solucionesL.replace("|", "\\textbar ");
		solucionesL = solucionesL.replace("\u2027", "·");
		solucionesL = solucionesL.replace("\u03B5", "$\\epsilon$");
		solucionesL = solucionesL.replace("$", "\\$ ");

		solucionesL = "\\begin{tabular} {| c | c | c | c |}\\hline\n"
				+ solucionesL;

		String expresion = problema.problema();
		expresion = expresion.replace("\u2027", "\\cdot ").replace("·", "\\cdot ").replace(".", "\\cdot ");
		expresion = expresion.replace("\u03B5", "\\epsilon ");
		expresion = expresion.replace("$", "\\$");
		expresion = expresion.replace("*", "^*");
		expresion = "$ " + expresion + " $";

		plantilla.set("expresion", expresion);
		plantilla.set("imagen", imagen);
		plantilla.set("tabla", solucionesL);

		return plantilla;
	}

	/**
	 * Traduce un problema de tipo AhoSethiUllman subtipo tablas a formato
	 * Latex.
	 * 
	 * @param problema
	 *            Problema AhoSethiUllman.
	 * @return Problema traducido a Latex.
	 */
	@Override
	public Plantilla traduceASUTablas(AhoSethiUllman problema) {
		log.info(
				"Traduciendo a Latex problema tipo Aho-Sethi-Ullman con expresión {}, formato tablas",
				problema.problema());

		StringBuilder stePos = new StringBuilder();
		StringBuilder fTrans = new StringBuilder();

		Plantilla plantilla = new Plantilla("plantillaASUTablas.tex");

		// siguiente-pos
		stePos.append("\\begin{tabular} {| c | l |}\n\\hline\nn & stePos(n) \\\\ \\hline\n");
		for (int n : problema.posiciones()) {
			stePos.append(n);
			stePos.append(" & ");
			stePos.append(setToRanges(problema.siguientePos(n)));
			stePos.append(" \\\\ \\hline\n");
		}
		stePos.append("\\end{tabular}");

		// Función de transición
		fTrans.append("\\begin{tabular} {| c | ");
		for (char simbolo : problema.simbolos())
			if (simbolo != '$')
				fTrans.append("c |");
		fTrans.append(" l |}\n\\hline \n& ");
		for (char simbolo : problema.simbolos())
			if (simbolo != '$')
				fTrans.append(simbolo + " & ");
		fTrans.append("\\\\ \\hline\n");

		for (char estado : problema.estados()) {
			if (problema.esFinal(estado))
				fTrans.append("(" + estado + ") & ");
			else
				fTrans.append(estado + " & ");
			for (char simbolo : problema.simbolos()) {
				if (simbolo != '$')
					fTrans.append(problema.mueve(estado, simbolo) + " & ");
			}
			fTrans.append(setToRanges(problema.estado(estado)));
			//for (int posicion : problema.estado(estado)) fTrans.append(posicion + " ");
			fTrans.append("\\\\ \\hline\n");
		}
		fTrans.append("\\end{tabular}");

		String expresion = problema.problema();
		expresion = expresion.replace("\u2027", "\\cdot ").replace("·", "\\cdot ").replace(".", "\\cdot ");
		expresion = expresion.replace("\u03B5", "\\epsilon ");
		expresion = expresion.replace("$", "\\$ ");
		expresion = expresion.replace("*", "^*");
		expresion = "$ " + expresion + " $";
		String expresionAumentada = problema.expresionAumentada();
		expresionAumentada = expresionAumentada.replace("\u2027", "\\cdot ").replace("·", "\\cdot ").replace(".", "\\cdot ");
		expresionAumentada = expresionAumentada.replace("\u03B5", "\\epsilon ");
		expresionAumentada = expresionAumentada.replace("$", "\\$ ");
		expresionAumentada = expresionAumentada.replace("*", "^*");
		expresionAumentada = "$ " + expresionAumentada + " $";

		plantilla.set("expresion", expresion);
		plantilla.set("aumentada", expresionAumentada);
		plantilla.set("siguientePos", stePos.toString());
		plantilla.set("transicion", fTrans.toString());

		return plantilla;
	}

	/**
	 * Traduce un problema de tipo construcción de subconjuntos subtipo
	 * construcción a formato Latex.
	 * 
	 * @param problema
	 *            Problema de construcción de subconjuntos.
	 * @return Problema traducido a Latex.
	 */
	@Override
	public Plantilla traduceCSConstruccion(ConstruccionSubconjuntos problema) {
		log.info("Traduciendo a Latex problema tipo construcción de subconjuntos con expresión {}, formato construcción",
				problema.problema());

		Plantilla plantilla = new Plantilla("plantillaCSConstruccion.tex");
		String imagen = problema.automata().hashCode() + "";
		
		String expresion = problema.problema();
		expresion = expresion.replace("\u2027", "\\cdot ").replace("·", "\\cdot ").replace(".", "\\cdot ");
		expresion = expresion.replace("\u03B5", "\\epsilon ");
		expresion = expresion.replace("$", "\\$ ");
		expresion = expresion.replace("*", "^*");
		expresion = "$ " + expresion + " $";
		
		plantilla.set("expresion", expresion);
		plantilla.set("imagen", imagen);

		return plantilla;
	}

	/**
	 * Traduce un problema de tipo construcción de subconjuntos subtipo
	 * expresión a formato Latex.
	 * 
	 * @param problema
	 *            Problema de construcción de subconjuntos.
	 * @return Problema traducido a Latex.
	 */
	@Override
	public Plantilla traduceCSExpresion(ConstruccionSubconjuntos problema) {
		log.info(
				"Traduciendo a Latex problema tipo construcción de subconjuntos con expresión {}, formato expresión",
				problema.problema());

		StringBuilder fTrans = new StringBuilder();

		Plantilla plantilla = new Plantilla("plantillaCSExpresion.tex");

		// Función de transición
		fTrans.append("\\begin{tabular} {| c | ");
		for (char simbolo : problema.simbolos())
			if (simbolo != '$')
				fTrans.append("c |");
		fTrans.append(" l |}\n\\hline \n& ");
		for (char simbolo : problema.simbolos())
			if (simbolo != '$')
				fTrans.append(simbolo + " & ");
		fTrans.append("\\\\ \\hline\n");

		for (char estado : problema.estados()) {
			if (problema.esFinal(estado))
				fTrans.append("(" + estado + ") & ");
			else
				fTrans.append(estado + " & ");
			for (char simbolo : problema.simbolos()) {
				if (simbolo != '$')
					fTrans.append(problema.mueve(estado, simbolo) + " & ");
			}
			fTrans.append(setToRanges(problema.posiciones(estado)));
			//for (int posicion : problema.posiciones(estado)) fTrans.append(posicion + " ");
			fTrans.append("\\\\ \\hline\n");
		}
		fTrans.append("\\end{tabular}");

		String expresion = problema.problema();
		expresion = expresion.replace("\u2027", "\\cdot ").replace("·", "\\cdot ").replace(".", "\\cdot ");
		expresion = expresion.replace("\u03B5", "\\epsilon ");
		expresion = expresion.replace("$", "\\$ ");
		expresion = expresion.replace("*", "^*");
		expresion = "$ " + expresion + " $";

		plantilla.set("expresion", expresion);
		plantilla.set("transicion", fTrans.toString());

		return plantilla;
	}

	/**
	 * Traduce un problema de tipo construcción de subconjuntos subtipo autómata
	 * a formato Latex.
	 * 
	 * @param problema
	 *            Problema de construcción de subconjuntos.
	 * @return Problema traducido a Latex.
	 */
	@Override
	public Plantilla traduceCSAutomata(ConstruccionSubconjuntos problema) {
		log.info(
				"Traduciendo a Latex problema tipo construcción de subconjuntos con expresión {}, formato autómata",
				problema.problema());

		String imagen = "" + problema.automata().hashCode();
		StringBuilder fTrans = new StringBuilder();

		Plantilla plantilla = new Plantilla("plantillaCSAutomata.tex");

		// Función de transición
		fTrans.append("\\begin{tabular} {| c | ");
		for (char simbolo : problema.simbolos())
			if (simbolo != '$')
				fTrans.append("c |");
		fTrans.append(" l |}\n\\hline \n& ");
		for (char simbolo : problema.simbolos())
			if (simbolo != '$')
				fTrans.append(simbolo + " & ");
		fTrans.append("\\\\ \\hline\n");

		for (char estado : problema.estados()) {
			if (problema.esFinal(estado))
				fTrans.append("(" + estado + ") & ");
			else
				fTrans.append(estado + " & ");
			for (char simbolo : problema.simbolos()) {
				if (simbolo != '$')
					fTrans.append(problema.mueve(estado, simbolo) + " & ");
			}
			fTrans.append(setToRanges(problema.posiciones(estado)));
			fTrans.append("\\\\ \\hline\n");
		}
		fTrans.append("\\end{tabular}");

		plantilla.set("imagen", imagen);
		plantilla.set("transicion", fTrans.toString());

		return plantilla;
	}

	/**
	 * Devuelve una representación de un conjunto de elementos separados con
	 * comas.
	 * 
	 * @param lista
	 *            Conjunto de elementos.
	 * @return Representación del conjunto como elementos separados por comas.
	 */
	@SuppressWarnings("unused")
	private String setToString(Set<?> lista) {
		StringBuilder setToString = new StringBuilder();

		if (lista.size() > 0) {
			String prefijo = "";
			for (Object elemento : lista) {
				setToString.append(prefijo);
				prefijo = ", ";
				setToString.append(elemento.toString());
			}
		} else {
			setToString.append("Cjto. vacío");
		}
		return setToString.toString();
	}

	/**
	 * Devuelve una representación de un conjunto de elementos como rangos separados por comas.
	 * 
	 * @param lista
	 *            Conjunto de elementos.
	 * @return Representación del conjunto como rangos separados por comas.
	 */
	// TODO: ¿se podría mover a la clase padre?
	private String setToRanges(Set<?> lista) {
		StringBuilder out = new StringBuilder();
		log.info("Ejecutando setToRanges({})", lista);
		int last=0, first=0, length=0; // inicializo para evitar warnings
		
		if (lista.size() == 0) {
			//out.append("\u2205"); // Unicode empty set
			out.append("$\\varnothing$"); // LaTeX empty set
		} else {
			String sep = "";
			boolean isFirst = true;
			for (Object e: lista) {
				if (isFirst) {
					last = first = (int) e;
					length = 1;
					isFirst = false;
					continue;
				}
				if ((int) e - last == 1) {
					last = (int) e;
					length++;
				} else {
					out.append(sep);
					sep = ", ";
					if (length == 1) {
						out.append(""+last);
					} else if (length == 2) {
						out.append(""+first+", "+last);
					} else {
						out.append(""+first+"-"+last);
					}
					last = first = (int) e;
					length = 1;
				}
			}
			out.append(sep);
			if (length == 1) {
				out.append(""+last);
			} else if (length == 2) {
				out.append(""+first+", "+last);
			} else {
				out.append(""+first+"-"+last);
			}
		}
		return out.toString();
	}

}
