package es.ubu.inf.tfg.doc;

import es.ubu.inf.tfg.regex.asu.AhoSethiUllman;

public interface Documento {
	public void aņadirProblema(AhoSethiUllman problema);
	public void eliminarProblema(AhoSethiUllman problema);
	public String toString();
}
