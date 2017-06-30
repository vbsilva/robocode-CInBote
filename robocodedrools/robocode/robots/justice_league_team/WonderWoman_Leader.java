/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package justice_league_team;

import java.util.Vector;
import java.awt.Color;
import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseFactory;
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.builder.ResourceType;
import org.drools.io.ResourceFactory;
import org.drools.runtime.StatefulKnowledgeSession;
import org.drools.runtime.rule.FactHandle;
import org.drools.runtime.rule.QueryResultsRow;

import justice_league_team.DEBUG;
import justice_league_team.EstadoBatalla;
import justice_league_team.EstadoRobot;
import robocode.BulletHitBulletEvent;
import robocode.BulletHitEvent;
import robocode.BulletMissedEvent;
import robocode.HitByBulletEvent;
import robocode.HitRobotEvent;
import robocode.HitWallEvent;
import robocode.RobotDeathEvent;
import robocode.ScannedRobotEvent;
import robocode.TeamRobot;

/**
 *
 * @author casc2
 */
public class WonderWoman_Leader extends TeamRobot {

    public static String FICHERO_REGLAS = "justice_league_team/reglas/reglas_robot_leader.drl";
    public static String CONSULTA_ACCIONES = "consulta_acciones";
    
    private KnowledgeBuilder kbuilder;
    private KnowledgeBase kbase;   // Base de conocimeintos
    private StatefulKnowledgeSession ksession;  // Memoria activa
    private Vector<FactHandle> referenciasHechosActuales = new Vector<FactHandle>();

    
    public WonderWoman_Leader(){
    }
    
    @Override
    public void run() {
    	DEBUG.habilitarModoDebug(System.getProperty("robot.debug", "true").equals("true"));    	

    	// Crear Base de Conocimiento y cargar reglas
    	crearBaseConocimiento();

        // Hacer que movimiento de tanque, radar y cañon sean independientes
        setAdjustGunForRobotTurn(true);
        setAdjustRadarForGunTurn(true);
        setAdjustRadarForRobotTurn(true);
        
        // Set colors (body, gun, radar, bullet, scan arc)
        setColors(Color.RED, Color.YELLOW, Color.BLUE, Color.BLUE, Color.YELLOW);

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
        String ficheroReglas = System.getProperty("robot.reglas", WonderWoman_Leader.FICHERO_REGLAS);

        DEBUG.mensaje("crear base de conocimientos");
        kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        
        DEBUG.mensaje("cargar reglas desde "+ficheroReglas);
        kbuilder.add(ResourceFactory.newClassPathResource(ficheroReglas, WonderWoman_Leader.class), ResourceType.DRL);
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

        for (QueryResultsRow resultado : ksession.getQueryResults(WonderWoman_Leader.CONSULTA_ACCIONES)) {
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
    public void onBulletHit(BulletHitEvent event) {
          referenciasHechosActuales.add(ksession.insert(event));
    }

    @Override
    public void onBulletHitBullet(BulletHitBulletEvent event) {
        referenciasHechosActuales.add(ksession.insert(event));
    }

    @Override
    public void onBulletMissed(BulletMissedEvent event) {
        referenciasHechosActuales.add(ksession.insert(event));
    }

    @Override
    public void onHitByBullet(HitByBulletEvent event) {
        referenciasHechosActuales.add(ksession.insert(event));
    }

    @Override
    public void onHitRobot(HitRobotEvent event) {
        referenciasHechosActuales.add(ksession.insert(event));
    }

    @Override
    public void onHitWall(HitWallEvent event) {
        referenciasHechosActuales.add(ksession.insert(event));
    }

    @Override
    public void onRobotDeath(RobotDeathEvent event) {
        referenciasHechosActuales.add(ksession.insert(event));
    }
    
    @Override
    public void onScannedRobot(ScannedRobotEvent event) {
        referenciasHechosActuales.add(ksession.insert(event));
    }

}
