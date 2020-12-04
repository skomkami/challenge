package agh.edu.pl.response

case class SearchResponse[+T](
    total: Long,
    hasNextPage: Boolean,
    results: Seq[T]
  )
