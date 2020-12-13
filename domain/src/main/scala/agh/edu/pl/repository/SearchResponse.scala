package agh.edu.pl.repository

case class SearchResponse[+T](
    total: Long,
    hasNextPage: Boolean,
    results: Seq[T]
  )
