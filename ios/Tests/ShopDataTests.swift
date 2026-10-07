import XCTest
@testable import PointfixSample

final class ShopDataTests: XCTestCase {
    func testProductSlugsAreUniqueSoIdentifiersStayStable() {
        let slugs = ShopData.products.map(\.slug)
        XCTAssertEqual(Set(slugs).count, slugs.count)
    }

    func testEveryCategoryButAllHasProducts() {
        for category in ShopCategory.allCases where category != .all {
            XCTAssertFalse(ShopData.products.filter { $0.category == category }.isEmpty, "\(category) is empty")
        }
    }

    func testCatalogKeepsTheLongNameThatShowsTheFlaw() {
        XCTAssertTrue(ShopData.products.contains { $0.name.count > 30 })
    }
}
