/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package justice_league_team;

import java.util.List;
import java.util.Vector;
import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseFactory;
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.builder.ResourceType;
import org.drools.io.ResourceFactory;
import org.drools.runtime.StatefulKnowledgeSession;
import org.drools.runtime.rule.FactHandle;
import org.drools.runtime.rule.QueryResultsRow;

import robocode.*;

/**
 *
 * @author casc2
 */
public class ComprobarReglas {

    public static String FICHERO_REGLAS = "justice_league_team/reglas/reglas_robot_leader.drl";
    public static String CONSULTA_ACCIONES = "consulta_acciones";
    private KnowledgeBuilder kbuilder;
    private KnowledgeBase kbase;                // Base de conocimientos
    private StatefulKnowledgeSession ksession;  // Memoria activa
    private Vector<FactHandle> referenciasHechosActuales = new Vector<FactHandle>();

    public ComprobarReglas() {
    	String modoDebug = System.getProperty("robot.debug", "true");
    	DEBUG.habilitarModoDebug(modoDebug.equals("true"));
        crearBaseConocimiento();
        cargarEventos();
    }

	public void cargarEventos() {
		ScannedRobotEvent e = new ScannedRobotEvent("pepe", 100, 10, 10, 10, 10);
        FactHandle referenciaHecho = ksession.insert(e);
        referenciasHechosActuales.add(referenciaHecho);
        // anadir mas hechos ....
        
        DEBUG.mensaje("hechos en memoria activa");
        DEBUG.volcarHechos(ksession);
        ksession.fireAllRules();
        List<Accion_Team> acciones = recuperarAcciones();
        DEBUG.mensaje("acciones resultantes");
        DEBUG.volcarAcciones(acciones);
        
	}

    private void crearBaseConocimiento() {
    	String ficheroReglas;
    	ficheroReglas = System.getProperty("robot.reglas", ComprobarReglas.FICHERO_REGLAS);
    	
    	DEBUG.mensaje("crear base de conocimientos");
        kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        
    	DEBUG.mensaje("cargar reglas desde "+ficheroReglas);
        kbuilder.add(ResourceFactory.newClassPathResource(ficheroReglas,ComprobarReglas.class), ResourceType.DRL);
        if (kbuilder.hasErrors()) {
            System.err.println(kbuilder.getErrors().toString());
        }

        kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addKnowledgePackages( kbuilder.getKnowledgePackages() );

        DEBUG.mensaje("crear sesion (memoria activa)");
        ksession = kbase.newStatefulKnowledgeSession();
    }

    public static void main(String args[]) {
        ComprobarReglas d = new ComprobarReglas();
    }

    private List<Accion_Team> recuperarAcciones() {
        Accion_Team accion;
        Vector<Accion_Team> listaAcciones = new Vector<Accion_Team>();

        for (QueryResultsRow resultado : ksession.getQueryResults(WonderWoman_Leader.CONSULTA_ACCIONES)) {
            accion = (Accion_Team) resultado.get("accion");  // Obtener el objeto accion
            accion.setRobot(null);                      // Vincularlo al robot actual
            listaAcciones.add(accion);
            ksession.retract(resultado.getFactHandle("accion")); // Eliminar el hecho de la memoria activa
        }

        return listaAcciones;
    }
}


