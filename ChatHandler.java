package ChatHandler;

import java.net.*;
import java.io.*;
import java.util.*;
public class ChatHandler extends Thread {
  // public ChatHandler (Socket s) throws IOException ...
  // public void run () ...
  protected Socket s;
  protected DataInputStream i;
  protected DataOutputStream o;
  public ChatHandler (Socket s) throws IOException {
    this.s = s;
    i = new DataInputStream (new BufferedInputStream (s.getInputStream ()));
    o = new DataOutputStream (new BufferedOutputStream (s.getOutputStream ()));
  }
  protected static Vector handlers = new Vector ();
  public void run () {
    try {
      handlers.addElement (this);
      while (true) {
        String msg = i.readUTF ();
        broadcast (msg);
      }
    } catch (IOException ex) {
      ex.printStackTrace ();
    } finally {
      handlers.removeElement (this);
      try {
        s.close ();
      } catch (IOException ex) {
        ex.printStackTrace();
      }
    }
  }
    protected static void broadcast (String message) {
    synchronized (handlers) {
      Enumeration e = handlers.elements ();
      while (e.hasMoreElements ()) {
        ChatHandler c = (ChatHandler) e.nextElement ();
        try {
          synchronized (c.o) {
            c.o.writeUTF (message);
          }
          c.o.flush ();
        } catch (IOException ex) {
          c.stop ();
        }
      }
    }
  }
}
