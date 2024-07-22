extends Node3D 

# Connection parameters
const PORT = 9080
var tcp_server = TCPServer.new()
var ws = WebSocketPeer.new()

func _ready():
	if tcp_server.listen(PORT) != OK:
		print("Unable to start the server.")
		set_process(false)
		

func _physics_process(delta):
	while tcp_server.is_connection_available():
		var conn = tcp_server.take_connection()
		assert(conn != null)
		ws.accept_stream(conn)

	ws.poll()

	if ws.get_ready_state() == WebSocketPeer.STATE_OPEN:
		while ws.get_available_packet_count():
			var msg = ws.get_packet().get_string_from_ascii()
			ws.send_text("Hello client!")
			
func _exit_tree():
	ws.close()
	tcp_server.stop()