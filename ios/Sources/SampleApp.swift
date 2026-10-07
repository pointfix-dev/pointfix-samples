import SwiftUI
import PointfixKit

@main
struct SampleApp: App {
    var body: some Scene {
        WindowGroup {
            SampleView()
                .pointfixScreen("Shop home")
                .pointfixHost()
        }
    }
}
