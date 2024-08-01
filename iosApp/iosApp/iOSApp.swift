import SwiftUI
import shared

@main
struct iOSApp: App {
    init() {
        let emptySet: KotlinArray<HelsDataSource<AnyObject>> = KotlinArray(size: 0) { index in nil }
        HelsInitializer().invoke(port: 1515, cachedHours: 8, additionalDataSources: emptySet)
    }
	var body: some Scene {
		WindowGroup {
			ContentView()
		}
	}
}
