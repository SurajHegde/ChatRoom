import java.net.* ;
import java.io.* ;
import java.awt.* ;
import java.awt.event.* ;
import java.util.* ;
import java.text.* ;
public class ChatClient extends Frame implements Runnable {

    protected DataInputStream input ;
    protected DataOutputStream output ;
    protected TextArea outputTextArea ;
    protected TextField inputTextField ;
    protected Thread listener ;
    protected String username ;

    public ChatClient( String title, InputStream i, OutputStream o, String uname) {
	super(title) ;
	this.input = new DataInputStream(new BufferedInputStream(i)) ;
	this.output = new DataOutputStream(new BufferedOutputStream(o)) ;
	this.username = uname ;
	setLayout(new BorderLayout()) ;
	add("Center", outputTextArea = new TextArea()) ;
	outputTextArea.setEditable(false) ;
	add("South", inputTextField = new TextField()) ;
	this.addWindowListener(new WindowAdapter(){
		public void windowClosing(WindowEvent we){
		    System.exit(0) ;
		}
	    }) ;
	pack() ;
	show() ;
	inputTextField.requestFocus() ;
	listener = new Thread( this) ;
	listener.start() ;
    }

    public void run() {
	try{
	    while(true){
		String line = input.readUTF() ;
		outputTextArea.appendText(line + "\n") ;
	    }
	}
	catch( IOException e){
	    e.printStackTrace() ;
	}
	finally {
	    listener = null ;
	    inputTextField.hide() ;
	    validate() ;
	    try{
		output.close() ;
	    }
	    catch (IOException e) {
		e.printStackTrace() ;
	    }
	}
    }

    public boolean handleEvent( Event e ){
	//	System.out.println("Event detected") ;
	if ((e.target == inputTextField) && (e.id == Event.ACTION_EVENT)){
	    try{
		System.out.println("Enter pressed") ;
		inputTextField.setText((String)"jdvhjdwshdj") ;
		Date d =  new Date() ;
		SimpleDateFormat df = new SimpleDateFormat("HH:mm:ss") ;

		output.writeUTF(df.format(d) + ">" + this.username +  ": " + (String) e.arg) ;
		output.flush() ;
	    }
	    catch (IOException er){
		er.printStackTrace() ;
		listener.stop() ;
	    }
	    inputTextField.setText("") ;
	    return true ;
	}
	else if( (e.target == this) && (e.id == Event.WINDOW_DESTROY) ){

	    if(listener != null)
		listener.stop() ;
	    hide() ;
	    return true ;
	}

	return super.handleEvent(e) ;
    }

    public static void main(String args[]) throws IOException {
	if(args.length != 3)
	    throw new RuntimeException("Syntax: ChatClient <host> <port> <username>") ;
	Socket s = new Socket(args[0], Integer.parseInt(args[1])) ;
	new ChatClient("Chat " + args[0] + ":" + args[1], s.getInputStream(), s.getOutputStream(), args[2]) ;
    }
}
