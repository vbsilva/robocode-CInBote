/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package justice_league_team;


import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseFactory;
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.builder.ResourceType;
import org.drools.io.ResourceFactory;
import org.drools.runtime.StatefulKnowledgeSession;
import org.drools.runtime.rule.FactHandle;
import org.drools.runtime.rule.QueryResultsRow;
import java.util.Vector;
import justice_league_team.DEBUG;
import justice_league_team.EstadoBatalla;
import justice_league_team.EstadoRobot;
import robocode.Droid;
import robocode.MessageEvent;
import robocode.TeamRobot;


public class Oracle extends TeamRobot implements Droid {
	public static String FICHERO_REGLAS = "justice_league_team/reglas/reglas_robot_droid.drl";
    public static String CONSULTA_ACCIONES = "consulta_acciones";
    
    private KnowledgeBuilder kbuilder;
    private KnowledgeBase kbase;   // Base de conocimeintos
    private StatefulKnowledgeSession ksession;  // Memoria activa
    private Vector<FactHandle> referenciasHechosActuales = new Vector<FactHandle>();

    
    public Oracle(){
    }
    
    @Override
    public void run() {
    	DEBUG.habilitarModoDebug(System.getProperty("robot.debug", "true").equals("true"));    	

    	// Crear Base de Conocimiento y cargar reglas
    	crearBaseConocimiento();

        // Hacer que movimiento de tanque y cañon sean independientes
        setAdjustGunForRobotTurn(true);
    	
        while (true) {
        	DEBUG.mensaje("inicio turno");

            //cargarEventos();  // se hace en los metodos onXXXXXEvent()
            cargarEstadoRobot();
            cargarEstadoBatalla();
        	
            // Lanzar reglas
            DEBUG.mensaje("hechos en memoria activa");
            DEBUG.volcarHechos(ksession);           
            ksession.fireAllRules();
            limpiarHechosIteracionAnterior();

            // Recuperar acciones
            Vector<Accion_Team> acciones = recuperarAcciones();
            DEBUG.mensaje("acciones resultantes");
            DEBUG.volcarAcciones(acciones);

            // Ejecutar Acciones
            ejecutarAcciones(acciones);
        	DEBUG.mensaje("fin turno\n");
            execute();  // Informa a robocode del fin del turno (llamada bloqueante)

        }

    }

    private void crearBaseConocimiento() {
        String ficheroReglas = System.getProperty("robot.reglas", Oracle.FICHERO_REGLAS);

        DEBUG.mensaje("crear base de conocimientos");
        kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        
        DEBUG.mensaje("cargar reglas desde "+ficheroReglas);
        kbuilder.add(ResourceFactory.newClassPathResource(ficheroReglas, Oracle.class), ResourceType.DRL);
        if (kbuilder.hasErrors()) {
            System.err.println(kbuilder.getErrors().toString());
        }

        kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addKnowledgePackages(kbuilder.getKnowledgePackages());
        
        DEBUG.mensaje("crear sesion (memoria activa)");
        ksession = kbase.newStatefulKnowledgeSession();
    }

    private void cargarEstadoRobot() {
    	EstadoRobot estadoRobot = new EstadoRobot(this);
        referenciasHechosActuales.add(ksession.insert(estadoRobot));
    }
    
    private void cargarEstadoBatalla() {
        EstadoBatalla estadoBatalla =
                new EstadoBatalla(getBattleFieldWidth(), getBattleFieldHeight(),
                getNumRounds(), getRoundNum(),
                getTime(),
                getOthers());
        referenciasHechosActuales.add(ksession.insert(estadoBatalla));
    }
    
    private void limpiarHechosIteracionAnterior() {
        for (FactHandle referenciaHecho : this.referenciasHechosActuales) {
            ksession.retract(referenciaHecho);
        }
        this.referenciasHechosActuales.clear();
    }

    private Vector<Accion_Team> recuperarAcciones() {
        Accion_Team accion;
        Vector<Accion_Team> listaAcciones = new Vector<Accion_Team>();

        for (QueryResultsRow resultado : ksession.getQueryResults(Oracle.CONSULTA_ACCIONES)) {
            accion = (Accion_Team) resultado.get("accion");  // Obtener el objeto accion
            accion.setRobot(this);                      // Vincularlo al robot actual
            listaAcciones.add(accion);
            ksession.retract(resultado.getFactHandle("accion")); // Eliminar el hecho de la memoria activa
        }

        return listaAcciones;
    }

    private void ejecutarAcciones(Vector<Accion_Team> acciones) {
        for (Accion_Team accion : acciones) {
            accion.iniciarEjecucion();
        }
    }

    // Insertar en la memoria activa los distintos tipos de eventos recibidos 
    @Override
    public void onMessageReceived(MessageEvent event) {
          referenciasHechosActuales.add(ksession.insert(event));
    }
    
}
