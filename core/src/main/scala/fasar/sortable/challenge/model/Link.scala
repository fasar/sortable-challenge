package fasar.sortable.challenge.model

case class Link(product: Product,
                items: List[Item]) {

  def +(item: Item) = {
    Link(product, item :: items)
  }
}
