/*
Yaaic - Yet Another Android IRC Client

Copyright 2009-2013 Sebastian Kaspari

This file is part of Yaaic.

Yaaic is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

Yaaic is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with Yaaic.  If not, see <http://www.gnu.org/licenses/>.
 */
package indrora.atomic.command.handler;

import indrora.atomic.command.BaseHandler;
import indrora.atomic.exception.CommandException;
import indrora.atomic.irc.IRCService;
import indrora.atomic.model.Broadcast;
import indrora.atomic.model.Conversation;
import indrora.atomic.model.Message;
import indrora.atomic.model.Server;

import java.io.File;

import indrora.atomic.R;

import android.content.Context;

/**
 * Command: /dcc SEND <nickname> <file>
 * <p/>
 * Send a file to a user
 *
 * @author Sebastian Kaspari <sebastian@yaaic.org>
 */
public class DCCHandler extends BaseHandler {
  /**
   * Execute /dcc
   */
  @Override
  public void execute(String[] params, Server server, Conversation conversation, IRCService service) throws CommandException {
    if( params.length == 4 ) {
      if( !params[1].equalsIgnoreCase("SEND") ) {
        throw new CommandException(service.getString(R.string.dcc_only_send));
      }
      File file = new File(params[3]);
      if( !file.exists() ) {
        throw new CommandException(service.getString(R.string.dcc_file_not_found, params[3]));
      }

      service.getConnection(server.getId()).dccSendFile(file, params[2], 60000);

      Message message = new Message(service.getString(R.string.dcc_waiting_accept, params[2]));
      message.setColor(Message.MessageColor.SERVER_EVENT);
      conversation.addMessage(message);

      service.sendBroadcast(
          Broadcast.createConversationIntent(Broadcast.CONVERSATION_MESSAGE, server.getId(), conversation.getName())
      );
    } else {
      throw new CommandException(service.getString(R.string.invalid_number_of_params));
    }
  }

  /**
   * Usage of /dcc
   */
  @Override
  public String getUsage() {
    return "/dcc SEND <nickname> <file>";
  }

  /**
   * Description of /dcc
   */
  @Override
  public String getDescription(Context context) {
    return context.getString(R.string.command_desc_dcc);
  }
}
