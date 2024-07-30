import SwiftUI
import shared

@main
struct iOSApp: App {
    init() {
        let emptySet: KotlinArray<HelsDataSource<AnyObject>> = KotlinArray(size: 0) { index in nil }
        HelsInitializer().invoke(port: 1515, cachedHours: 8, additionalDataSources: emptySet)
        
        var params = [String:String]()
        params["key1"] = "val1"
        params["key2"] = "val2"
        GlobalsKt.HelsLog.d(tag: "crazy", message: "fuck", properties: params)
    }
	var body: some Scene {
		WindowGroup {
			ContentView()
		}
	}
}
