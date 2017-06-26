/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package justice_league_team;

import java.io.IOException;

import robocode.TeamRobot;

/**
 *
 * @author casc2
 */
public class Accion_Team {
    private int        tipo;
    private double     parametro;
    private int        prioridad;

    private TeamRobot robot;   // Referncia al robot que ejecutara la accion

    public static final int AVANZAR=1;
    public static final int RETROCEDER=2;
    public static final int STOP=3;
    public static final int DISPARAR=4;
    public static final int GIRAR_TANQUE_DER=5;
    public static final int GIRAR_TANQUE_IZQ=6;
    public static final int GIRAR_RADAR_DER=7;
    public static final int GIRAR_RADAR_IZQ=8;
    public static final int GIRAR_CANON_DER=9;
    public static final int GIRAR_CANON_IZQ=10;
    public static final int BROADCAST=11;


    public Accion_Team() {
    }

    public Accion_Team(int tipo, double parametro, int prioridad) {
        this.tipo = tipo;
        this.parametro = parametro;
        this.prioridad = prioridad;
    }

    public double getParametro() {
        return parametro;
    }

    public void setParametro(double parametro) {
        this.parametro = parametro; 
    }

    public int getTipo() {
        return tipo;
    }

    public void setTipo(int tipo) {
        this.tipo = tipo;
    }

    public int getPrioridad() {
        return prioridad;
    }

    public void setPrioridad(int prioridad) {
        this.prioridad = prioridad;
    }



    public void iniciarEjecucion() {
        if (this.robot != null) {
            switch (this.tipo) {
                case Accion_Team.DISPARAR: robot.setFire(parametro); break;
                case Accion_Team.AVANZAR: robot.setAhead(parametro); break;
                case Accion_Team.RETROCEDER: robot.setBack(parametro); break;
                case Accion_Team.STOP: robot.setStop(); break;
                case Accion_Team.GIRAR_CANON_DER: robot.setTurnGunRight(parametro); break;
                case Accion_Team.GIRAR_CANON_IZQ: robot.setTurnGunLeft(parametro); break;
                case Accion_Team.GIRAR_RADAR_DER: robot.setTurnRadarRight(parametro); break;
                case Accion_Team.GIRAR_RADAR_IZQ: robot.setTurnRadarLeft(parametro); break;
                case Accion_Team.GIRAR_TANQUE_DER: robot.setTurnRight(parametro); break;
                case Accion_Team.GIRAR_TANQUE_IZQ: robot.setTurnLeft(parametro); break;
                case Accion_Team.BROADCAST: try {
					robot.broadcastMessage(parametro);
				} catch (IOException e) {
					e.printStackTrace();
				} break;
            }
        }
    }

    void setRobot(TeamRobot robot) {
        this.robot = robot;
    }

    public String toString(){
        String etqTipo="";
            switch (this.tipo) {
                case Accion_Team.DISPARAR:etqTipo="Disparar"; break;
                case Accion_Team.AVANZAR: etqTipo="Avanzar"; break;
                case Accion_Team.RETROCEDER: etqTipo="Retroceder"; break;
                case Accion_Team.STOP: etqTipo="Stop"; break;
                case Accion_Team.GIRAR_CANON_DER: etqTipo="Girar cañón derecha"; break;
                case Accion_Team.GIRAR_CANON_IZQ: etqTipo="Girar cañón izquierda"; break;
                case Accion_Team.GIRAR_RADAR_DER: etqTipo="Girar radar derecha"; break;
                case Accion_Team.GIRAR_RADAR_IZQ: etqTipo="Girar radar izquierda"; break;
                case Accion_Team.GIRAR_TANQUE_DER: etqTipo="Girar tanque derecha"; break;
                case Accion_Team.GIRAR_TANQUE_IZQ: etqTipo="Girar tanque izquierda"; break;
                case Accion_Team.BROADCAST: etqTipo="Broadcast message"; break;
            }
	return "Accion[tipo:"+etqTipo+", param:"+parametro+", prioridad:"+prioridad+"]";
    }

}
