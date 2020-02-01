package com.tst

object Pricing {
  type RateCode = String
  type RateGroup = String
  type CabinCode = String

  def main(args: Array[String]): Unit = {
    val rates = Seq(
      Rate("M1", "Military"),
      Rate("M2", "Military"),
      Rate("S1", "Senior"),
      Rate("S2", "Senior")
    )
    val prices = Seq(
      CabinPrice("CA", "M1", 200.00),
      CabinPrice("CA", "M2", 250.00),
      CabinPrice("CA", "S1", 225.00),
      CabinPrice("CA", "S2", 260.00),
      CabinPrice("CB", "M1", 230.00),
      CabinPrice("CB", "M2", 260.00),
      CabinPrice("CB", "S1", 245.00),
      CabinPrice("CB", "S2", 270.00)
    )
    println(new PricingEngine().getBestGroupPrices(rates, prices))
  }
}

case class Rate(rateCode: String,
                rateGroup: String)

case class CabinPrice(cabinCode: String,
                      rateCode: String,
                      price: BigDecimal)

case class BestGroupPrice(cabinCode: String,
                          rateCode: String,
                          price: BigDecimal,
                          rateGroup: String)

class PricingEngine {
  import Pricing._

  def getBestGroupPrices(rates: Seq[Rate],
                         prices: Seq[CabinPrice]): Seq[BestGroupPrice] = {
    // CB has everything output needs save rateGroup, and that's determined directly from rateCode
    // Key rates by code so we can go from code->group
    // Group CB by rateGroup (looked up from its code), sort by price, take the head, that's it!
    val ratesByCode: Map[RateCode, RateGroup] = rates.map(rate => (rate.rateCode, rate.rateGroup)).toMap
    val pricesByCabinAndRateGroup: Map[(CabinCode, RateGroup), Seq[CabinPrice]] = prices.groupBy(price => (price.cabinCode, ratesByCode(price.rateCode)))
    pricesByCabinAndRateGroup.map { case ((cabinCode, rateGroup), pricesForGroup) =>
      val sorted: ((CabinCode, RateGroup), Seq[CabinPrice]) = ((cabinCode, rateGroup), pricesForGroup.sortBy(_.price))
      val bestPrice = sorted._2.head
      BestGroupPrice(cabinCode, bestPrice.rateCode, bestPrice.price, rateGroup)
    }.toSeq.sortBy(_.price)
  }
}
