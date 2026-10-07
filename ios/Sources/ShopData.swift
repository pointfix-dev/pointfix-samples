import SwiftUI

enum ShopCategory: String, CaseIterable, Identifiable {
    case all = "All", dresses = "Dresses", jackets = "Jackets", hoodies = "Hoodies", jeans = "Jeans", shoes = "Shoes"
    var id: Self { self }
}

struct Product: Identifiable {
    let slug: String
    let name: String
    let category: ShopCategory
    let line: String
    let price: Double
    var oldPrice: Double? = nil
    let symbol: String
    let tint: Color
    var id: String { slug }
}

struct Promo {
    let eyebrow: String
    let title: String
    let highlight: String
    let symbol: String
}

extension Color {
    static let lime = Color(red: 0.545, green: 0.878, blue: 0.353)
    static let ink = Color(light: .init(white: 0.08, alpha: 1), dark: .init(white: 0.95, alpha: 1))
    static let surface = Color(light: .init(white: 0.955, alpha: 1), dark: .init(white: 0.0, alpha: 1))
    static let card = Color(light: .white, dark: .init(white: 0.11, alpha: 1))
    static let bannerInk = Color(light: .init(white: 0.09, alpha: 1), dark: .init(white: 0.15, alpha: 1))

    init(light: UIColor, dark: UIColor) {
        self.init(uiColor: UIColor { $0.userInterfaceStyle == .dark ? dark : light })
    }
}

enum ShopData {
    static let promos = [
        Promo(eyebrow: "Super Sale Discount", title: "Up to", highlight: "50%", symbol: "tshirt.fill"),
        Promo(eyebrow: "New Season Sneakers", title: "Fresh", highlight: "drops", symbol: "shoe.fill"),
        Promo(eyebrow: "Members Weekend", title: "Extra", highlight: "20% off", symbol: "handbag.fill"),
    ]

    static let products = [
        Product(slug: "wool-overcoat", name: "Oversized Double-Breasted Wool Overcoat", category: .jackets, line: "Outerwear Women", price: 149, oldPrice: 189, symbol: "hanger", tint: .orange),
        Product(slug: "linen-midi-dress", name: "Linen Midi Dress", category: .dresses, line: "Dresses Women", price: 64, oldPrice: 80, symbol: "figure.stand.dress", tint: .pink),
        Product(slug: "cloud-hoodie", name: "Cloud Fleece Hoodie", category: .hoodies, line: "Hoodies Unisex", price: 52, symbol: "tshirt.fill", tint: .green),
        Product(slug: "straight-jeans", name: "Straight Leg Jeans", category: .jeans, line: "Denim Men", price: 68, oldPrice: 85, symbol: "hanger", tint: .blue),
        Product(slug: "runner-sneakers", name: "Air Runner Sneakers", category: .shoes, line: "Footwear Unisex", price: 110, symbol: "shoe.fill", tint: .purple),
        Product(slug: "wrap-dress", name: "Floral Wrap Dress", category: .dresses, line: "Dresses Women", price: 72, symbol: "figure.stand.dress", tint: .red),
        Product(slug: "bomber-jacket", name: "Satin Bomber Jacket", category: .jackets, line: "Outerwear Men", price: 95, oldPrice: 120, symbol: "tshirt", tint: .teal),
        Product(slug: "zip-hoodie", name: "Boxy Zip Hoodie", category: .hoodies, line: "Hoodies Men", price: 58, symbol: "tshirt.fill", tint: .gray),
        Product(slug: "wide-jeans", name: "Wide Leg Jeans", category: .jeans, line: "Denim Women", price: 74, symbol: "hanger", tint: .indigo),
        Product(slug: "chelsea-boots", name: "Chelsea Boots", category: .shoes, line: "Footwear Women", price: 135, oldPrice: 160, symbol: "shoe.2.fill", tint: .brown),
    ]
}
