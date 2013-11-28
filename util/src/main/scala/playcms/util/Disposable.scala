package playcms.util

import scala.util.Try

trait IDisposable {
  def dispose(): Unit
}

object Disposable {
  def using[A](disposable: => IDisposable)(block: IDisposable => A): A =
    Try(block(disposable)).fold(
      onSuccess = { a =>
        disposable.dispose()
        a
      },
      onFailure = { t =>
        disposable.dispose()
        throw t
      }
    )
}
