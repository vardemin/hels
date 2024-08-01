import Foundation
import Alamofire
import shared

func fetchData() {
    let reqId = GlobalsKt.HelsRequest.generateId()
    AF.request("https://catfact.ninja/fact").response { response in
        //log request
        GlobalsKt.HelsRequest.logRequest(
            id: reqId,
            method: response.request?.httpMethod ?? "",
            url: response.request?.url?.absoluteString ?? "",
            headers: response.request?.headers.dictionary ?? [String: String](),
            payload: jsonString(fromData: response.request?.httpBody)
        )
        if response.error != nil {
            // failure - log error
            GlobalsKt.HelsRequest.logError(
                id: reqId,
                message: response.error?.errorDescription ?? ""
            )
            return
        }

        // happy path - log response
        GlobalsKt.HelsRequest.logResponse(
            id: reqId,
            code: Int32(response.response?.statusCode ?? -1),
            headers: response.response?.headers.dictionary ?? [String: String](),
            payload: jsonString(fromData: response.data)
        )
    }
}

func jsonString(fromData: Data?) -> String? {
    guard let fromData = fromData, !fromData.isEmpty else { return nil }
    return String(data: fromData, encoding: String.Encoding.utf8)
}
