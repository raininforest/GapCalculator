import SwiftUI

struct ContentView: View {
	var body: some View {
		NavigationView {
			GapListScreen()
		}
		.navigationViewStyle(StackNavigationViewStyle())
	}
}

struct ContentView_Previews: PreviewProvider {
	static var previews: some View {
		ContentView()
	}
}
