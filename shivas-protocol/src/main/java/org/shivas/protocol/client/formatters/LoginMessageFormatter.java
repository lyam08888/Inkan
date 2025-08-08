package org.shivas.protocol.client.formatters;

import org.shivas.protocol.client.types.BaseCharactersServerType;
import org.shivas.protocol.client.types.GameServerType;

import java.util.Collection;

/**
 * User: Blackrush
 * Date: 30/10/11
 * Time: 10:18
 * IDE : IntelliJ IDEA
 */
public class LoginMessageFormatter {
    public static String helloConnect(String ticket){
        return "HC" + ticket;
    }

    public static String badClientVersion(String requiredVersion){
        return "AlEv" + requiredVersion;
    }

    public static String accessDenied(){
        return "AlEf";
    }

    public static String banned(){
        return "AlEb";
    }

    public static String alreadyConnected(){
        return "AlEc";
    }

    public static String nicknameInformationMessage(String nickname) {
        return "Ad" + nickname;
    }

    public static String communityInformationMessage(int community) {
        return "Ac" + community;
    }

    public static String identificationSuccessMessage(boolean hasRights) {
        return "AlK" + (hasRights ? "1" : "0");
    }

    public static String accountQuestionInformationMessage(String question) {
        return "AQ" + question.replace(" ", "+");
    }

    public static String serversInformationsMessage(Collection<GameServerType> servers) {
        StringBuilder sb = new StringBuilder(servers.size() * 10).append("AH");

        boolean first = true;
        for (GameServerType gs : servers){
            if (!first)
                sb.append('|');
            else
                first = false;

            sb.append(gs.getId()).append(';')
              .append(gs.getState().ordinal()).append(';')
              .append(gs.getCompletion()).append(';')
              .append(gs.isJoinable() ? '1' : '0');
        }

        return sb.toString();
    }
    
    public static String serverInformationMessage(GameServerType server) {
    	return String.format("AH%d;%d;%d;%s",
    			server.getId(),
    			server.getState().ordinal(),
    			server.getCompletion(),
    			server.isJoinable() ? '1' : '0'
		);
    }

    public static String charactersListMessage(long subscribeTimeEnd, Collection<BaseCharactersServerType> characters) {
        StringBuilder sb = new StringBuilder(10 + characters.size() * 5).append("AxK").append(subscribeTimeEnd);

        for (BaseCharactersServerType entry : characters){
            sb.append('|')
              .append(entry.getServerId()).append(',')
              .append(entry.getCharacters());
        }

        return sb.toString();
    }
    
    public static String charactersListMessage(long subscribeTimeEnd, int serverId, int characters) {
    	return String.format("AxK%d|%d,%d", subscribeTimeEnd, serverId, characters);
    }

    public static String selectedHostInformationMessage(String address, int port, String ticket, boolean loopback) {
        return "AYK" + (loopback ? "127.0.0.1" : address) + ":" + port + ";" + ticket;
    }

    public static String serverSelectionErrorMessage() {
        return "AYE";
    }
}
