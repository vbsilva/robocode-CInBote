package justice_league_team;

import java.util.List;
import java.util.Vector;

import org.drools.runtime.StatefulKnowledgeSession;
import org.drools.runtime.rule.FactHandle;

import justice_league_team.Accion_Team;

public final class DEBUG {
	public static boolean modoDebugHabilitado = false;

	public static void habilitarModoDebug(boolean b) {
		modoDebugHabilitado = b;
	}

	public static void mensaje(String string) {
		if (modoDebugHabilitado) {
			System.out.println("DEBUG:"+string);			
		}		
	}

	public static void volcarHechos(StatefulKnowledgeSession ksession) {
		if (modoDebugHabilitado){
			for (FactHandle f: ksession.getFactHandles()){
				System.out.println("  "+ksession.getObject(f));				
			}			
		}		
	}

	public static void volcarAcciones(List<Accion_Team> acciones) {
		if (modoDebugHabilitado){
			for (Accion_Team a: acciones){
				System.out.println("  "+a.toString());				
			}
		}
	}
}
