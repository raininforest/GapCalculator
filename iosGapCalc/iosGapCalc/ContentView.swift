import SwiftUI

let MAX_CONTENT_WIDTH = 640.0

struct ContentView: View {
	var body: some View {
		// on an iPad the screens stay a phone-width column instead of stretching across the display
		ZStack {
			Colors.lightGray.ignoresSafeArea()
			NavigationView {
				GapListScreen()
			}
			.navigationViewStyle(StackNavigationViewStyle())
			.frame(maxWidth: MAX_CONTENT_WIDTH)
		}
	}
}

struct ContentView_Previews: PreviewProvider {
	static var previews: some View {
		ContentView()
	}
}
