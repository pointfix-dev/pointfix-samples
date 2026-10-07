import SwiftUI

enum ShopTab: String, CaseIterable {
    case home = "Home", bag = "Bag", favorites = "Favorites", profile = "Profile"
    var symbol: String {
        switch self {
        case .home: "house.fill"
        case .bag: "bag"
        case .favorites: "heart"
        case .profile: "person"
        }
    }
}

struct SampleView: View {
    @State private var query = ""
    @State private var category = ShopCategory.all
    @State private var favorites: Set<String> = ["linen-midi-dress"]
    @State private var bagCount = 2
    @State private var saleOnly = false
    @State private var tab = ShopTab.home

    private var visible: [Product] {
        ShopData.products.filter { p in
            (category == .all || p.category == category)
                && (!saleOnly || p.oldPrice != nil)
                && (tab != .favorites || favorites.contains(p.slug))
                && (query.isEmpty || p.name.localizedStandardContains(query) || p.line.localizedStandardContains(query))
        }
    }

    var body: some View {
        ScrollView {
            VStack(alignment: .leading, spacing: 22) {
                header
                search
                PromoBanner()
                chips
                sectionTitle
                grid
            }
            .padding(.horizontal, 20)
            .padding(.top, 8)
            .padding(.bottom, 24)
        }
        .scrollDismissesKeyboard(.immediately)
        .background(Color.surface.ignoresSafeArea())
        .safeAreaInset(edge: .bottom) { tabBar }
    }

    // MARK: Header

    private var header: some View {
        HStack(spacing: 12) {
            Text("AL")
                .font(.subheadline.weight(.bold))
                .foregroundStyle(.black)
                .frame(width: 46, height: 46)
                .background(Color.lime.gradient, in: .circle)
                .accessibilityHidden(true)
            VStack(alignment: .leading, spacing: 2) {
                Text("Welcome back").font(.caption).foregroundStyle(.secondary)
                HStack(spacing: 4) {
                    Text("Hey, Alex").font(.headline)
                    Image(systemName: "chevron.down").font(.caption.weight(.semibold)).foregroundStyle(.secondary)
                }
            }
            .accessibilityElement(children: .combine)
            .accessibilityIdentifier("header.greeting")
            Spacer()
            Button { tab = .bag } label: {
                Image(systemName: "bag")
                    .font(.system(size: 19, weight: .medium))
                    .foregroundStyle(Color.ink)
                    .symbolEffect(.bounce, value: bagCount)
                    .frame(width: 48, height: 48)
                    .background(Color.card, in: .circle)
                    .overlay(alignment: .topTrailing) {
                        Text("\(bagCount)")
                            .font(.caption2.weight(.bold))
                            .monospacedDigit()
                            .contentTransition(.numericText())
                            .foregroundStyle(.black)
                            .frame(minWidth: 18, minHeight: 18)
                            .background(Color.lime, in: .capsule)
                            .offset(x: 2, y: -2)
                            .accessibilityIdentifier("bag.count")
                    }
            }
            .buttonStyle(.plain)
            .accessibilityLabel("Bag, \(bagCount) items")
            .accessibilityIdentifier("header.bag")
        }
    }

    // MARK: Search

    private var search: some View {
        HStack(spacing: 12) {
            HStack(spacing: 10) {
                Image(systemName: "magnifyingglass").foregroundStyle(.secondary)
                TextField("Explore Fashion", text: $query)
                    .textInputAutocapitalization(.never)
                    .submitLabel(.search)
                    .accessibilityIdentifier("shop.search")
                if !query.isEmpty {
                    Button("Clear search", systemImage: "xmark.circle.fill") { query = "" }
                        .labelStyle(.iconOnly)
                        .foregroundStyle(.tertiary)
                }
            }
            .padding(.horizontal, 16)
            .frame(height: 54)
            .background(Color.card, in: .rect(cornerRadius: 18))

            Button { withAnimation(.snappy) { saleOnly.toggle() } } label: {
                Image(systemName: saleOnly ? "tag.fill" : "slider.horizontal.3")
                    .font(.system(size: 18, weight: .semibold))
                    .contentTransition(.symbolEffect(.replace))
                    .foregroundStyle(.black)
                    .frame(width: 54, height: 54)
                    .background(Color.lime, in: .rect(cornerRadius: 18))
            }
            .buttonStyle(.plain)
            .accessibilityLabel(saleOnly ? "Showing sale items only" : "Filter")
            .accessibilityIdentifier("shop.filter")
        }
    }

    // MARK: Categories

    private var chips: some View {
        ScrollView(.horizontal) {
            HStack(spacing: 10) {
                ForEach(ShopCategory.allCases) { c in
                    let selected = c == category
                    Button { withAnimation(.snappy) { category = c } } label: {
                        HStack(spacing: 6) {
                            if selected { Image(systemName: "sparkles").foregroundStyle(Color.lime) }
                            Text(c.rawValue)
                        }
                        .font(.subheadline.weight(selected ? .semibold : .medium))
                        .foregroundStyle(selected ? Color.card : Color.secondary)
                        .padding(.horizontal, 18)
                        .frame(height: 42)
                        .background(selected ? Color.ink : Color.card, in: .capsule)
                    }
                    .buttonStyle(.plain)
                    .accessibilityAddTraits(selected ? .isSelected : [])
                    .accessibilityIdentifier("category.\(c.rawValue.lowercased())")
                }
            }
        }
        .scrollIndicators(.hidden)
        .scrollClipDisabled()
    }

    private var sectionTitle: some View {
        HStack(alignment: .firstTextBaseline) {
            Text(tab == .favorites ? "Your Favorites" : "Special For You")
                .font(.title3.weight(.bold))
            Spacer()
            Button("See All") {
                withAnimation(.snappy) { category = .all; query = ""; saleOnly = false; tab = .home }
            }
            .font(.subheadline.weight(.medium))
            .foregroundStyle(.secondary)
            .accessibilityIdentifier("shop.seeAll")
        }
    }

    // MARK: Grid

    @ViewBuilder private var grid: some View {
        let items = visible
        if items.isEmpty {
            ContentUnavailableView("No matches", systemImage: "hanger", description: Text("Try another search or category."))
                .frame(maxWidth: .infinity)
                .padding(.vertical, 32)
        } else {
            LazyVGrid(columns: [GridItem(.flexible(), spacing: 14), GridItem(.flexible(), spacing: 14)], spacing: 14) {
                ForEach(items) { p in
                    ProductCard(
                        product: p,
                        isFavorite: favorites.contains(p.slug),
                        toggleFavorite: { favorites.formSymmetricDifference([p.slug]) },
                        add: { withAnimation(.snappy) { bagCount += 1 } }
                    )
                }
            }
        }
    }

    // MARK: Tab bar

    private var tabBar: some View {
        HStack(spacing: 4) {
            ForEach(ShopTab.allCases, id: \.self) { t in
                let active = t == tab
                Button { withAnimation(.snappy) { tab = t } } label: {
                    HStack(spacing: 6) {
                        Image(systemName: active && t != .home ? t.symbol + ".fill" : t.symbol)
                        if active { Text(t.rawValue).font(.subheadline.weight(.semibold)).fixedSize() }
                    }
                    .font(.system(size: 18, weight: .medium))
                    .foregroundStyle(active ? Color.black : Color.white.opacity(0.6))
                    .frame(height: 52)
                    .padding(.horizontal, active ? 20 : 0)
                    .frame(maxWidth: active ? nil : .infinity)
                    .background(active ? Color.lime : .clear, in: .capsule)
                    .contentShape(.capsule)
                }
                .buttonStyle(.plain)
                .layoutPriority(active ? 1 : 0)
                .accessibilityLabel(t.rawValue)
                .accessibilityAddTraits(active ? .isSelected : [])
                .accessibilityIdentifier("nav.\(t.rawValue.lowercased())")
            }
        }
        .padding(6)
        .background(Color.bannerInk, in: .capsule)
        .shadow(color: .black.opacity(0.18), radius: 16, y: 8)
        .padding(.horizontal, 20)
        .padding(.bottom, 4)
    }
}

// MARK: - Banner

private struct PromoBanner: View {
    @State private var page = 0

    var body: some View {
        ZStack(alignment: .bottomLeading) {
            TabView(selection: $page) {
                ForEach(ShopData.promos.indices, id: \.self) { i in
                    slide(ShopData.promos[i]).tag(i)
                }
            }
            .tabViewStyle(.page(indexDisplayMode: .never))

            // Kept outside the pager so it has one stable identity for capture.
            Button {} label: {
                Text("Shop Now")
                    .font(.subheadline.weight(.semibold))
                    .foregroundStyle(.black)
                    .padding(.horizontal, 20)
                    .frame(height: 40)
                    .background(Color.lime, in: .capsule)
            }
            .buttonStyle(.plain)
            .accessibilityIdentifier("checkout.continue")
            .padding(20)

            HStack(spacing: 5) {
                ForEach(ShopData.promos.indices, id: \.self) { i in
                    Capsule()
                        .fill(i == page ? Color.lime : .white.opacity(0.3))
                        .frame(width: i == page ? 18 : 6, height: 6)
                }
            }
            .frame(maxWidth: .infinity, alignment: .trailing)
            .padding(.trailing, 20)
            .padding(.bottom, 20)
            .accessibilityHidden(true)
        }
        .frame(height: 188)
        .background(Color.bannerInk, in: .rect(cornerRadius: 24))
        .clipShape(.rect(cornerRadius: 24))
        .task {
            while !Task.isCancelled {
                try? await Task.sleep(for: .seconds(5))
                withAnimation(.easeInOut(duration: 0.6)) { page = (page + 1) % ShopData.promos.count }
            }
        }
    }

    private func slide(_ promo: Promo) -> some View {
        ZStack(alignment: .topLeading) {
            art(promo.symbol)
                .frame(maxWidth: .infinity, maxHeight: .infinity, alignment: .trailing)
            VStack(alignment: .leading, spacing: 4) {
                Text(promo.eyebrow)
                    .font(.subheadline.weight(.medium))
                    .foregroundStyle(.white.opacity(0.7))
                Text("\(promo.title) \(Text(promo.highlight).foregroundStyle(Color.lime))")
                    .foregroundStyle(.white)
                    .font(.system(size: 30, weight: .bold))
            }
            .padding(20)
            .accessibilityElement(children: .combine)
        }
    }

    private func art(_ symbol: String) -> some View {
        ZStack {
            ForEach(0..<4) { i in
                Circle()
                    .stroke(Color.lime.opacity(0.45 - Double(i) * 0.1), lineWidth: 1.5)
                    .frame(width: 120 + CGFloat(i) * 46)
            }
            Image(systemName: symbol)
                .font(.system(size: 64, weight: .regular))
                .foregroundStyle(Color.lime.gradient)
                .rotationEffect(.degrees(-8))
        }
        .frame(width: 150)
        .offset(x: 40, y: -6)
        .accessibilityHidden(true)
    }
}

// MARK: - Product card

private struct ProductCard: View {
    let product: Product
    let isFavorite: Bool
    let toggleFavorite: () -> Void
    let add: () -> Void

    var body: some View {
        VStack(alignment: .leading, spacing: 10) {
            ZStack(alignment: .topTrailing) {
                RoundedRectangle(cornerRadius: 18)
                    .fill(product.tint.opacity(0.16))
                    .overlay {
                        Image(systemName: product.symbol)
                            .font(.system(size: 58, weight: .light))
                            .foregroundStyle(product.tint.gradient)
                    }
                    .frame(height: 150)
                    .accessibilityHidden(true)
                Button(action: toggleFavorite) {
                    Image(systemName: isFavorite ? "heart.fill" : "heart")
                        .font(.system(size: 15, weight: .semibold))
                        .foregroundStyle(isFavorite ? Color.red : Color.ink)
                        .contentTransition(.symbolEffect(.replace))
                        .symbolEffect(.bounce, value: isFavorite)
                        .frame(width: 34, height: 34)
                        .background(Color.card, in: .circle)
                }
                .buttonStyle(.plain)
                .padding(8)
                .accessibilityLabel(isFavorite ? "Remove \(product.name) from favorites" : "Add \(product.name) to favorites")
                .accessibilityAddTraits(isFavorite ? .isSelected : [])
                .accessibilityIdentifier("product.\(product.slug).favorite")
            }

            VStack(alignment: .leading, spacing: 3) {
                Text(product.line)
                    .font(.caption)
                    .foregroundStyle(.secondary)
                // Intentional demo flaw: lineLimit(1) chops long names ("Oversized Double-Breasted…")
                // instead of wrapping to two lines; report it via Pointfix to see the fix flow.
                Text(product.name)
                    .font(.subheadline.weight(.semibold))
                    .lineLimit(1)
                    .accessibilityIdentifier("product.\(product.slug).name")
            }
            .padding(.horizontal, 4)

            HStack(alignment: .firstTextBaseline, spacing: 6) {
                Text("$\(Int(product.price))")
                    .font(.headline)
                if let old = product.oldPrice {
                    Text("$\(Int(old))")
                        .font(.caption)
                        .strikethrough()
                        .foregroundStyle(.secondary)
                        .accessibilityLabel("was \(Int(old)) dollars")
                }
                Spacer(minLength: 4)
                Button(action: add) {
                    Image(systemName: "plus")
                        .font(.system(size: 13, weight: .bold))
                        .foregroundStyle(Color.card)
                        .frame(width: 30, height: 30)
                        .background(Color.ink, in: .circle)
                }
                .buttonStyle(.plain)
                .alignmentGuide(.firstTextBaseline) { $0[VerticalAlignment.center] + 5 }
                .accessibilityLabel("Add \(product.name) to bag")
                .accessibilityIdentifier("product.\(product.slug).add")
            }
            .padding(.horizontal, 4)
        }
        .padding(8)
        .padding(.bottom, 4)
        .background(Color.card, in: .rect(cornerRadius: 22))
    }
}

#Preview { SampleView() }
