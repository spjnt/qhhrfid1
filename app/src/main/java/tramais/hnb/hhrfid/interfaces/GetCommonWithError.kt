package tramais.hnb.hhrfid.interfaces

interface GetCommonWithError<T> {
    fun getCommon(t: T)
    fun getError()
}