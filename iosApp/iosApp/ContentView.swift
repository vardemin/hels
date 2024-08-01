import SwiftUI
import shared

struct ContentView: View {
    @State private var debugMessage: String = ""
    @State private var infoMessage: String = ""
	
    var body: some View {
        VStack {
            HStack {
                Button(action: { sendDebug()}) {
                    Text("Debug")
                        .frame(maxWidth: .infinity)
                        .padding()
                        .background(Color.blue)
                        .foregroundColor(.white)
                        .cornerRadius(8)
                }
                .padding()
                TextField("Debug msg", text: $debugMessage)
                    .padding()
                
            }
            HStack {
                Button(action: { sendInfo()}) {
                    Text("Info")
                        .frame(maxWidth: .infinity)
                        .padding()
                        .background(Color.blue)
                        .foregroundColor(.white)
                        .cornerRadius(8)
                }
                .padding()
                TextField("Debug msg", text: $infoMessage)
                    .padding()
                
            }
            Button(action: { doNetworkCall()}) {
                Text("Network call")
                    .frame(maxWidth: .infinity)
                    .padding()
                    .background(Color.blue)
                    .foregroundColor(.white)
                    .cornerRadius(8)
            }
            .padding()
            Button(action: { serverStop()}) {
                Text("Server stop")
                    .frame(maxWidth: .infinity)
                    .padding()
                    .background(Color.blue)
                    .foregroundColor(.white)
                    .cornerRadius(8)
            }
            .padding()
            Button(action: { serverStart()}) {
                Text("Server start")
                    .frame(maxWidth: .infinity)
                    .padding()
                    .background(Color.blue)
                    .foregroundColor(.white)
                    .cornerRadius(8)
            }
            .padding()
        }
        .padding()
	}
    
    private func sendDebug() {
        var params = [String:String]()
        params["key1"] = "val1"
        params["key2"] = "val2"
        GlobalsKt.HelsLog.d(tag: "crazy", message: debugMessage, properties: params)
        
        debugMessage = ""
    }
    
    private func sendInfo() {
        var params = [String:String]()
        params["key1"] = "val1"
        params["key2"] = "val2"
        GlobalsKt.HelsLog.i(tag: "crazy", message: infoMessage, properties: params)
        
        infoMessage = ""
    }
    
    private func doNetworkCall() {
        fetchData()
    }
    
    private func serverStart() {
        GlobalsKt.HelServer.start()
    }
    
    private func serverStop() {
        GlobalsKt.HelServer.stop()
    }
}

struct ContentView_Previews: PreviewProvider {
	static var previews: some View {
		ContentView()
	}
}
