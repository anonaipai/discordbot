package defaulte;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.Scanner;
import java.util.TreeMap;
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.common.util.concurrent.FutureCallback;

import de.btobastian.javacord.DiscordAPI;
import de.btobastian.javacord.Javacord;
import de.btobastian.javacord.entities.Channel;
import de.btobastian.javacord.entities.User;
import de.btobastian.javacord.entities.message.Message;
import de.btobastian.javacord.listener.message.MessageCreateListener;
import de.btobastian.javacord.listener.message.TypingStartListener;

public class discordbot {
	public discordbot(String token) {
		// See "How to get the token" below
		DiscordAPI api = Javacord.getApi(token, true);
		// connect
		api.connect(new FutureCallback<DiscordAPI>() {
			public void onSuccess(final DiscordAPI api) {
				// do what you want now
				api.registerListener(new ambushListener());
				api.registerListener(new pdListener());
			}

			public void onFailure(Throwable t) {
				// login failed
				t.printStackTrace();
			}
		});
	}

	public static void main(String args[]) {
		discordbot bob = new discordbot();
	}
}

class ambushListener implements TypingStartListener {
	public static TreeMap<String, String> ambushList;

	public ambushListener() {
		ambushList = new TreeMap<String, String>();
		try {
			FileWriter f = new FileWriter("/src/ambushlist");
		} catch (IOException e) {
			System.out.println("filenotfound");
		}
	}

	@Override
	public void onTypingStart(DiscordAPI arg0, User arg1, Channel arg2) {
		// TODO Auto-generated method stub
		if (ambushList.get(arg1.getId()) != null) {
			arg2.sendMessage(ambushList.get(arg1.getId()));
			ambushList.remove(arg1.getId());
		}

	}

}

class pdListener implements MessageCreateListener {
	static Scanner sc = new Scanner(System.in);
	final static String path = "src/shattered-pixel-dungeon-master/core/src/main/java/com/shatteredpixel/shatteredpixeldungeon/items/";

	public void onMessageCreate(DiscordAPI arg0, Message arg1) {
		System.out.println(arg1);
		String msgStr = arg1.getContent();
		if (msgStr.matches("!ambush <@!?\\d+> .+")) {
			addAmbush(arg0, arg1);
		}
		if (arg1.isPrivateMessage()) {
			if (arg1.getContent().matches("disarm")) {
				if (ambushListener.ambushList.remove(arg1.getAuthor().getId()) != null) {
					arg1.reply("disarmed");
				} else {
					arg1.reply("No ambush found");
				}
			}
		}
	}

	public static void addAmbush(DiscordAPI arg0, Message arg1) {
		Pattern p = Pattern.compile("!ambush <@!?(\\d+)> (.+)");
		Matcher m = p.matcher(arg1.getContent());
		if (m.matches()) {
			try {
				arg1.reply("Ambush set for " + arg0.getUserById(m.group(1)).get().getName());
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ExecutionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			ambushListener.ambushList.put(m.group(1),
					"<@" + arg1.getAuthor().getId() + "> told me to tell you " + m.group(2));
		} else {
			System.out.println("how did I get here?");
		}
		arg1.delete();
	}
}
